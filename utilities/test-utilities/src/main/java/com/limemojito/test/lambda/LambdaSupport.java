/*
 * Copyright 2011-2024 Lime Mojito Pty Ltd
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.limemojito.test.lambda;

import com.fasterxml.jackson.core.type.TypeReference;
import com.limemojito.test.jackson.JacksonSupport;
import com.limemojito.test.s3.S3Support;
import jakarta.annotation.PreDestroy;
import jakarta.validation.constraints.Min;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.function.adapter.aws.FunctionInvoker;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.utils.IoUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.awaitility.Durations.FIVE_HUNDRED_MILLISECONDS;
import static software.amazon.awssdk.services.lambda.model.Runtime.JAVA17;
import static software.amazon.awssdk.services.lambda.model.Runtime.NODEJS20_X;

/**
 * <p>Be aware that creating lambdas on localstack uses containers on the underlying docker infrastructure.
 * Use cleanup() to clean any left over lambdas after deployments.  You can do this in a test in an @AfterEach,
 * etc.</p>
 *
 *
 * <p>We setup the _JAVA_OPTIONS here to nudge localstack into debug mode.  We add the standard lambda
 * JAVA_TOOL_OPTIONS as well if set by the user.  If not set we stop compilation at interpreted only to reduce
 * cold start.  Production will use SnapStart and different settings.</p>
 *
 * @see #cleanup()
 */
@Service
@Slf4j
public class LambdaSupport {
    private static final int TWO_KB = 2048;
    private static final int MB = 1024 * 1024;
    private static final int NO_DEBUG = -1;

    /**
     * Raw java execution timout seconds (180).  This gives 3 minutes to covers non SnapStart deployments.
     */
    public static final int JAVA_EXECUTION_TIMEOUT = 180;

    /**
     * Raw javaDebug execution timout seconds (900).  This gives 15 minutes to debug in localstack before a retry.
     *
     * @see #javaDebug(String, String, Map)
     */
    public static final int JAVA_DEBUG_EXECUTION_TIMEOUT = 900;

    /**
     * Default localstack lambda role (stubbed IAM)
     */
    public static final String LAMBDA_ROLE = "arn:aws:iam::000000000000:role/lambda-role";

    /**
     * Default port for debugging.
     */
    public static final int DEFAULT_DEBUG_PORT = 5050;

    /**
     * Default java memory size.
     */
    public static final int DEFAULT_JAVA_MEMORY_MEGABYTES = 1024;

    /**
     * Default stub (javascript) memory size.
     */
    public static final int STUB_MEMORY = 256 * MB;

    /**
     * Default debug VM setup options
     */
    public static final String DEBUG_OPS = "-Xshare:off -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=0.0.0.0:%d";

    /**
     * Name of default handler from spring-cloud-function.
     */
    public static final String SPRING_CLOUD_FUNCTION_HANDLER = FunctionInvoker.class.getName();

    private static final String FUNCTION_DEF_KEY = "SPRING_CLOUD_FUNCTION_DEFINITION";

    /**
     * As used in the maven base pom /jar-lambda-development/pom.xml
     */
    private static final String LAMBDA_JAR_CLASSIFIER = "aws";

    private final LambdaClient lambdaClient;
    private final S3Support s3;
    private final JacksonSupport json;
    private final int deployTimeoutSeconds;
    private final Set<String> tracker;
    private final Set<String> mappingTracker;

    /**
     * The Lambda function is identified by its name and Amazon Resource Name (ARN).
     * This class serves as a convenient way to handle and manipulate Lambda function details.
     */
    public record Lambda(String name, String arn) {
    }

    /**
     * Initializes the LambdaSupport object with the provided dependencies and deploy timeout.
     *
     * @param lambdaClient         The LambdaClient instance used for interacting with AWS Lambda.
     * @param s3                   The S3Support instance used for interacting with AWS S3.
     * @param json                 The JacksonSupport instance used for JSON processing.
     * @param deployTimeoutSeconds The deploy timeout in seconds. Defaults to 180 if not specified.
     */
    public LambdaSupport(LambdaClient lambdaClient,
                         S3Support s3,
                         JacksonSupport json,
                         @Value("${lambda.support.deploy.timeoutSeconds:180}") int deployTimeoutSeconds) {
        this.lambdaClient = lambdaClient;
        this.s3 = s3;
        this.json = json;
        this.deployTimeoutSeconds = deployTimeoutSeconds;
        this.tracker = new HashSet<>();
        this.mappingTracker = new HashSet<>();
        log.info("Deploy timeout at {} seconds", deployTimeoutSeconds);
    }

    /**
     * Invoke for lambda events that use custom json parsing from AWS.
     *
     * @param lambda       lambda to call
     * @param lambdaEvent  event to convert
     * @param responseType event to convert to (assume Lambda Event)
     * @param <T>          Type to expect for response.
     * @return instance of the return type from the lambda.
     * @see com.amazonaws.services.lambda.runtime.serialization.events.LambdaEventSerializers
     */
    public <T> T invokeLambdaEvent(Lambda lambda, Object lambdaEvent, Class<T> responseType) {
        String jsonRequest = json.toJsonLambdaEvent(lambdaEvent);
        String responseJson = invokeAllowFail(lambda, jsonRequest);
        return json.parseLambdaEvent(responseJson, responseType);
    }

    /**
     * Invokes a Lambda function asynchronously with the specified input and returns the response.
     *
     * @param lambda       the Lambda function to invoke
     * @param pojo         the input object to be converted to JSON
     * @param responseType the class representing the type of the response object
     * @param <T>          the type of the response object
     * @return the response object parsed from the JSON string
     */
    public <T> T invoke(Lambda lambda, Object pojo, Class<T> responseType) {
        String jsonString = json.toJson(pojo);
        String responseJson = invokeAllowFail(lambda, jsonString);
        return json.parse(responseJson, responseType);
    }

    /**
     * Invokes the specified lambda function with the given POJO object as input
     * and returns the response converted to the specified response object type.
     *
     * @param lambda       the lambda function to invoke
     * @param pojo         the POJO object to pass as input to the lambda function
     * @param responseType the type of the response object to convert the response to
     * @param <T>          the type of the response object
     * @return the response object converted to the specified type
     */
    public <T> T invoke(Lambda lambda, Object pojo, TypeReference<T> responseType) {
        String jsonString = json.toJson(pojo);
        String responseJson = invokeAllowFail(lambda, jsonString);
        return json.parse(responseJson, responseType);
    }

    /**
     * Invoke with pure json string request and response.
     *
     * @param lambda     Lambda to invoke
     * @param jsonString the input string
     * @return the output string
     */
    public String invokeAllowFail(Lambda lambda, String jsonString) {
        log.debug("Invoking {} with json {}", lambda.arn, jsonString);
        InvokeResponse response = lambdaClient.invoke(r -> r.functionName(lambda.name)
                                                            .payload(SdkBytes.fromUtf8String(jsonString)));
        String responseJson = response.payload().asUtf8String();
        log.debug("Response: {}", responseJson);
        String errorMessage = response.functionError();
        if (errorMessage != null) {
            throw new RuntimeException("Lambda %s failed with %s".formatted(lambda, errorMessage));
        }
        return responseJson;
    }

    /**
     * Creates a Lambda function object using the provided function name by retrieving data from the Lambda runtime.
     *
     * @param functionName the name of the Lambda function
     * @return a Lambda object representing the function
     */
    public Lambda forName(String functionName) {
        return new Lambda(functionName, describe(functionName).configuration().functionArn());
    }

    /**
     * Creates a lambda stub with the given name and response data.
     * The response data is serialized to JSON format using the provided JSON library.
     * The created lambda stub can be used in integration tests.
     *
     * @param name         The name of the lambda stub.
     * @param responsePojo The response data object to be serialized to JSON format.
     * @return A lambda stub with the given name and serialized response data.
     */
    @SneakyThrows
    public Lambda stub(String name, Object responsePojo) {
        return stub(name, json.toJson(responsePojo));
    }

    /**
     * Creates a lambda stub with the given name and response data.
     * The response data is serialized to JSON format using the provided JSON library.
     * The created lambda stub can be used in integration tests.
     *
     * @param name     The name of the lambda stub.
     * @param response The response JSON.
     * @return A lambda stub with the given name and response data.
     */
    public Lambda stub(String name, String response) {
        return deployStub(name, response, false);
    }

    /**
     * Creates a lambda stub with the given name and response data as a failure event.
     * The response data is serialized to JSON format using the provided JSON library.
     * The created lambda stub can be used in integration tests.
     *
     * @param name         The name of the lambda stub.
     * @param responsePojo The response data object to be serialized to JSON format.
     * @return A lambda stub with the given name and response data as a fail event.
     */
    @SneakyThrows
    public Lambda stubFail(String name, Object responsePojo) {
        return stubFail(name, json.toJson(responsePojo));
    }

    /**
     * Creates a lambda stub with the given name and response data as a failure event.
     * The response data is serialized to JSON format using the provided JSON library.
     * The created lambda stub can be used in integration tests.
     *
     * @param name     The name of the lambda stub.
     * @param response The response JSON.
     * @return A lambda stub with the given name and response JSON as a fail event.
     */
    public Lambda stubFail(String name, String response) {
        return deployStub(name, response, true);
    }

    /**
     * Deploys Java lambda with sane defaults such as 1GB Memory.  To debug use the deprecated javaDebug method but
     * do not commit this code as it will pause the deployed lambda until a debug connection is made.
     *
     * @param moduleLocation Maven module directory to deploy (relative path to this module).
     * @param handler        Java class name fully qualified as the Lambda Stream Handler
     * @param environment    Map of environment variables to deploy.  May be decorated with more.
     * @return the deployed lambda.
     * @see #SPRING_CLOUD_FUNCTION_HANDLER
     */
    public Lambda java(String moduleLocation,
                       String handler,
                       Map<String, String> environment) {
        return java(moduleLocation, handler, DEFAULT_JAVA_MEMORY_MEGABYTES, environment);
    }

    /**
     * Deploys Java lambda with sane defaults.  To debug use the deprecated javaDebug method but
     * do not commit this code as it will pause the deployed lambda until a debug connection is made.
     *
     * <p>AWS Lambda vCPU is proportional to memory.  Though for execution time consider that it is normally
     * single threaded.  AWS keeps the number of CPUs vs Memory ranking internal.</p>
     *
     * @param moduleLocation  Maven module directory to deploy (relative path to this module).
     * @param handler         Java class name fully qualified as the Lambda Stream Handler
     * @param memoryMegabytes Number of MB to allocate to lambda.  AWS vCPU is proportional to memory.
     * @param environment     Map of environment variables to deploy.  May be decorated with more.
     * @return the deployed lambda.
     * @see #SPRING_CLOUD_FUNCTION_HANDLER
     */
    public Lambda java(String moduleLocation,
                       String handler,
                       @Min(DEFAULT_JAVA_MEMORY_MEGABYTES) int memoryMegabytes,
                       Map<String, String> environment) {
        return deployJavaFromSourceBase(moduleLocation, handler, memoryMegabytes, environment, NO_DEBUG);
    }

    /**
     * Debug should not be called directly as we pause for server connections by default
     *
     * @param moduleLocation Maven module directory to deploy (relative path to this module).
     * @param handler        Java class name fully qualified as the Lambda Stream Handler
     * @param environment    Map of environment variables to deploy.  May be decorated with more.
     * @return the deployed lambda.
     * @see #SPRING_CLOUD_FUNCTION_HANDLER
     * @deprecated DO NOT USE IN COMMITTED CODE AS THIS STOPS THE VM UNTIL DEBUG CONNECTION.
     */
    @Deprecated
    public Lambda javaDebug(String moduleLocation,
                            String handler,
                            Map<String, String> environment) {
        return javaDebug(moduleLocation, handler, DEFAULT_JAVA_MEMORY_MEGABYTES, environment, DEFAULT_DEBUG_PORT);
    }

    /**
     * Debug should not be called directly as we pause for server connections by default.  Refer to readme.md.
     *
     * @param moduleLocation  Maven module directory to deploy (relative path to this module).
     * @param handler         Java class name fully qualified as the Lambda Stream Handler
     * @param memoryMegabytes Number of MB to allocate to lambda.  AWS vCPU is proportional to memory.
     * @param environment     Map of environment variables to deploy.  May be decorated with more.
     * @param debugPort       port for debugging to occur on (when > 0).  Must align with docker-compose.yml file.
     * @return the deployed lambda.
     * @see #SPRING_CLOUD_FUNCTION_HANDLER
     * @see #DEFAULT_DEBUG_PORT
     * @deprecated DO NOT USE IN COMMITTED CODE AS THIS STOPS THE VM UNTIL DEBUG CONNECTION.
     */
    @Deprecated
    public Lambda javaDebug(String moduleLocation,
                            String handler,
                            @Min(DEFAULT_JAVA_MEMORY_MEGABYTES) int memoryMegabytes,
                            Map<String, String> environment,
                            @Min(1) int debugPort) {
        return deployJavaFromSourceBase(moduleLocation, handler, memoryMegabytes, environment, debugPort);
    }

    /**
     * Cleans up the resources used by the deployed lambdas.
     * <p>
     * This method is annotated with the `@PreDestroy` annotation, indicating that it is executed
     * before the object is destroyed.
     * <p>
     * The cleanup process involves the following steps:
     * <p>
     * 1. Logs a warning message, indicating the number of deployed lambdas to be cleaned up.
     * 2. Iterates over the `mappingTracker` collection and invokes the `safeDeleteMapping` method
     * for each entry.
     * 3. Clears the `mappingTracker` collection.
     * 4. Iterates over the `tracker` collection and invokes the `safeDelete` method for each entry.
     * 5. Clears the `tracker` collection.
     * <p>
     * Please note that this method does not return any value.
     *
     * @see PreDestroy
     */
    @PreDestroy
    public void cleanup() {
        log.warn("Cleaning up deployed lambdas (container dregs) {} lambdas", tracker.size());
        mappingTracker.forEach(this::safeDeleteMapping);
        mappingTracker.clear();
        tracker.forEach(this::safeDelete);
        tracker.clear();
    }

    /**
     * Maps an event source to a lambda using configuration defaults.  Note JSON should be loaded and consumed
     * using xxxLambdaEvent methods of JsonSupport.
     *
     * @param sourceArn   ARN of aws service generating events.
     * @param destination Lambda that is the destination of events.
     * @see JacksonSupport#loadLambdaEvent(String, Class)
     */
    public void addEventSourceTo(String sourceArn, Lambda destination) {
        CreateEventSourceMappingResponse mapping = lambdaClient.createEventSourceMapping(r -> r.functionName(destination.name())
                                                                                               .eventSourceArn(sourceArn)
                                                                                               .enabled(true));
        mappingTracker.add(mapping.uuid());
    }

    /**
     * Waits for lambda state using the deployment timeout seconds (default 300).  Overridden
     * on construction of LambdaSupport using spring property lambda.support.deploy.timeoutSeconds
     *
     * @param lambda lambda to wait for
     * @param state  state to wait for
     */
    public void waitForState(Lambda lambda, State state) {
        waitForState(lambda, deployTimeoutSeconds, state);
    }

    /**
     * Waits for lambda state using the deployment timeout seconds (default 300).  Overridden
     * on construction of LambdaSupport using spring property lambda.support.deploy.timeoutSeconds
     *
     * @param lambda     lambda to wait for
     * @param maxSeconds Maximum seconds to wait.
     * @param state      state to wait for
     */
    public void waitForState(Lambda lambda, int maxSeconds, State state) {
        Awaitility.waitAtMost(maxSeconds, TimeUnit.SECONDS)
                  .pollInterval(FIVE_HUNDRED_MILLISECONDS)
                  .alias("%s did not reach state %s".formatted(lambda.name, state))
                  .until(() -> checkFailed(lambda, state));
    }

    /**
     * Debug should not be called directly as we pause for server connections by default.  This class is aware of
     * spring cloud functions and will suffix the name with the SPRING_CLOUD_FUNCTION_DEFINITION if set in the
     * environment.
     *
     * @param moduleLocation  Maven module directory to deploy (relative path to this module).
     * @param handler         Java class name fully qualified as the Lambda Stream Handler
     * @param memoryMegabytes Number of MB to allocate to lambda.  AWS vCPU is proportional to memory.
     * @param environment     Map of environment variables to deploy.  May be decorated with more.
     * @param debugPort       port for debugging to occur on (when > 0)
     * @return the deployed lambda.
     * @see #SPRING_CLOUD_FUNCTION_HANDLER
     */
    private Lambda deployJavaFromSourceBase(String moduleLocation,
                                            String handler,
                                            @Min(DEFAULT_JAVA_MEMORY_MEGABYTES) int memoryMegabytes,
                                            Map<String, String> environment,
                                            int debugPort) {

        final String artifactId = moduleLocation.substring(moduleLocation.lastIndexOf('/') + 1);
        return deployJava(artifactId,
                          () -> findJar(moduleLocation),
                          handler, memoryMegabytes,
                          debugPort,
                          environment
        );
    }

    private Lambda deployJava(String artifactId,
                              Supplier<byte[]> getCodeJar,
                              String handler,
                              int memoryMegabytes,
                              int debugPort,
                              Map<String, String> environment) {
        final byte[] awsJar = getCodeJar.get();
        final String deployBucket = "lambda-deploy";
        final String name = lambdaName(environment, artifactId);
        final String key = "%s.jar".formatted(name);
        final URI s3Uri = s3.toS3Uri(deployBucket, key);
        log.info("Uploading code to {}", s3Uri);
        s3.createBucket(deployBucket);
        s3.putData(s3Uri, "application/zip", awsJar);
        final Integer timeout = isInDebugMode(debugPort) ? JAVA_DEBUG_EXECUTION_TIMEOUT : JAVA_EXECUTION_TIMEOUT;
        log.info("""
                         Deploying Java AWS function {} with
                         \ttimeout: {} seconds
                         \thandler {}
                         \tenv {}""", name, timeout, handler, environment);
        final String desc = "Deployment of %s from artifactId %s with timeout %d (s)".formatted(name,
                                                                                                artifactId,
                                                                                                timeout);
        return deploy(name, r -> r.functionName(name)
                                  .description(desc)
                                  .memorySize(memoryMegabytes * MB)
                                  .handler(handler)
                                  .runtime(JAVA17)
                                  .environment(e -> e.variables(spyEnvironment(environment,
                                                                               debugPort,
                                                                               memoryMegabytes)))
                                  .code(c -> c.s3Bucket(deployBucket).s3Key(key))
                                  .packageType("Zip")
                                  .role(LAMBDA_ROLE)
                                  .timeout(timeout)
        );
    }

    private static String lambdaName(Map<String, String> environment, String artifactId) {
        return "%s%s".formatted(
                artifactId,
                environment.containsKey(FUNCTION_DEF_KEY) ? "-" + environment.get(FUNCTION_DEF_KEY)
                                                          : "");
    }

    private void safeDelete(String name) {
        try {
            lambdaClient.deleteFunction(r -> r.functionName(name));
        } catch (Exception e) {
            log.warn("Error deleting function {}: {} {}", name, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void safeDeleteMapping(String uuid) {
        try {
            lambdaClient.deleteEventSourceMapping(c -> c.uuid(uuid));
        } catch (Exception e) {
            log.warn("Error deleting mapping for {}: {}", uuid, e.getMessage());
        }
    }


    @SneakyThrows
    private byte[] findJar(String moduleLocation) {
        log.info("Uploading code from {}", moduleLocation);
        File moduleBaseDir = new File(moduleLocation);
        File targetDir = new File(moduleBaseDir, "target");
        if (!targetDir.isDirectory()) {
            throw new IOException("Can not access module target directory %s".formatted(targetDir));
        }
        final String suffix = "-%s.jar".formatted(LAMBDA_JAR_CLASSIFIER);
        String[] list = targetDir.list((dir, file) -> file.endsWith(suffix));
        if (list == null || list.length != 1) {
            throw new IOException("Incorrect jars (not ending with %s) detected %s".formatted(suffix, list));
        }
        try (FileInputStream input = new FileInputStream(new File(targetDir, list[0]))) {
            return IoUtils.toByteArray(input);
        }
    }

    private Map<String, String> spyEnvironment(Map<String, String> environment,
                                               int debugPort,
                                               int memoryMegabytes) {
        log.debug("Supplied Environment is {}", environment);
        String javaToolOptions = computeToolOptions(environment, memoryMegabytes);
        Map<String, String> spyEnv = new LinkedHashMap<>(environment);
        final boolean inDebugMode = isInDebugMode(debugPort);
        if (inDebugMode) {
            log.warn(
                    """
                            ***
                            \tWARNING JAVA LAMBDA DEBUG ENABLED ON LOCALHOST PORT {}.
                            \tThis can cause port clashes.
                            \tDebug port must match JVM_DEBUG_PORT in docker-compose.yml
                            ***""",
                    debugPort);
        }
        spyEnv.put("_JAVA_OPTIONS",
                   "%s %s".formatted(inDebugMode
                                     ? DEBUG_OPS.formatted(debugPort)
                                     : "",
                                     javaToolOptions));
        log.debug("Decorated Environment is {}", spyEnv);
        return spyEnv;
    }

    private static String computeToolOptions(Map<String, String> environment, int memoryMegabytes) {
        final String javaOptionsKey = "JAVA_TOOL_OPTIONS";
        String javaToolOptions = environment.get(javaOptionsKey);
        if (javaToolOptions == null) {
            javaToolOptions = environment.getOrDefault(javaOptionsKey,
                                                       "-XX:+TieredCompilation -XX:TieredStopAtLevel=1");
        }
        final double eightyPercent = 0.8;
        final int maxHeap = (int) (memoryMegabytes * eightyPercent);
        log.debug("Defaulting Localstack Lambda Max Heap to 80% of configured memory: {} MB", maxHeap);
        javaToolOptions += " -Xms%dm -Xmx%dm".formatted(maxHeap, maxHeap);
        return javaToolOptions;
    }

    private Lambda deployStub(String name, String response, boolean failure) {
        log.info("Creating stub function {} with result {}", name, response);
        return deploy(name, stubConfiguration(name, response, failure));
    }

    private boolean checkFailed(Lambda lambda, State state) {
        FunctionConfiguration configuration = describe(lambda.name).configuration();
        State currentState = configuration.state();
        String stateReason = configuration.stateReason() == null ? "" : configuration.stateReason();
        if (currentState == State.FAILED) {
            log.error("Lambda {} failed with {}", lambda.name, stateReason);
        } else {
            log.debug("Lambda {} deployment in state {} {}", lambda.name, currentState, stateReason);
        }
        return currentState == state;
    }


    private Consumer<CreateFunctionRequest.Builder> stubConfiguration(String name, String response, boolean failure) {
        return r -> r.functionName(name)
                     .description("Stub for %s".formatted(name))
                     .runtime(NODEJS20_X)
                     .handler("index.handler")
                     .memorySize(STUB_MEMORY)
                     .code(c -> dynamicStubCode(c, response, failure))
                     .packageType(PackageType.ZIP)
                     .role(LAMBDA_ROLE);
    }


    private GetFunctionResponse describe(String name) {
        return lambdaClient.getFunction(r -> r.functionName(name));
    }

    @SneakyThrows
    private void dynamicStubCode(FunctionCode.Builder c, String response, boolean failure) {
        String nodeJsCode = """
                exports.handler = async function(event) {
                    if (%b) {
                        throw new Error('%s')
                    }
                    return JSON.parse('%s')
                };
                """.formatted(failure, response, response);
        // inline hack.
        try (ByteArrayOutputStream outputBytes = new ByteArrayOutputStream(TWO_KB);
             ZipOutputStream codeZip = new ZipOutputStream(outputBytes)) {
            codeZip.putNextEntry(new ZipEntry("index.js"));
            codeZip.write(nodeJsCode.getBytes(UTF_8));
            codeZip.finish();
            c.zipFile(SdkBytes.fromByteArray(outputBytes.toByteArray()));
        }
    }

    private Lambda deploy(String name, Consumer<CreateFunctionRequest.Builder> stubRequest) {
        try {
            return performDeploy(name, stubRequest);
        } catch (ResourceConflictException e) {
            log.info("Replacing existing function {}", name);
            safeDelete(name);
            return performDeploy(name, stubRequest);
        }

    }

    private static boolean isInDebugMode(int debugPort) {
        return debugPort > 0;
    }

    private Lambda performDeploy(String name, Consumer<CreateFunctionRequest.Builder> stubRequest) {
        CreateFunctionResponse function = lambdaClient.createFunction(stubRequest);
        log.debug("Pending lambda deployment from {}", function.functionArn());
        tracker.add(name);
        Lambda deployed = new Lambda(name, function.functionArn());
        waitForState(deployed, deployTimeoutSeconds, State.ACTIVE);
        log.info("Deployed lambda {}", deployed);
        return deployed;
    }

}
