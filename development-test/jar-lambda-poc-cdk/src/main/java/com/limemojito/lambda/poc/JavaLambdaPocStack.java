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

package com.limemojito.lambda.poc;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.aws_apigatewayv2_integrations.HttpLambdaIntegration;
import software.amazon.awscdk.aws_apigatewayv2_integrations.HttpLambdaIntegrationProps;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.*;
import software.amazon.awscdk.services.iam.IManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.VersionProps;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

import static com.limemojito.aws.lambda.LimeAwsLambdaConfiguration.LAMBDA_HANDLER;
import static software.amazon.awscdk.services.apigatewayv2.PayloadFormatVersion.VERSION_2_0;
import static software.amazon.awscdk.services.iam.ManagedPolicy.fromAwsManagedPolicyName;
import static software.amazon.awscdk.services.lambda.Architecture.X86_64;
import static software.amazon.awscdk.services.lambda.Code.fromAsset;
import static software.amazon.awscdk.services.lambda.Runtime.JAVA_17;

@Slf4j
public class JavaLambdaPocStack extends Stack {

    static final String LAMBDA_FUNCTION_ID = "jar-lambda-poc";
    private static final String LAMBDA_CODE_PATH = "target/dependency/%s.jar".formatted(LAMBDA_FUNCTION_ID);

    public JavaLambdaPocStack(Construct scope,
                              String id,
                              StackProps props) {
        super(scope, id, props);

        final List<IManagedPolicy> managedPolicies =
                List.of(fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole"));

        final Role role = Role.Builder.create(this, LAMBDA_FUNCTION_ID + "-role")
                                      .assumedBy(new ServicePrincipal("lambda.amazonaws.com"))
                                      .managedPolicies(managedPolicies)
                                      .build();

        final AssetCode assetCode = fromAsset(LAMBDA_CODE_PATH);

        final int memorySize = 1024;
        final int timeoutSeconds = 30;
        final Function function = new Function(this,
                                               LAMBDA_FUNCTION_ID,
                                               FunctionProps.builder()
                                                            .functionName(LAMBDA_FUNCTION_ID)
                                                            .description("Lambda example with Java 17")
                                                            .role(role)
                                                            .timeout(Duration.seconds(timeoutSeconds))
                                                            .memorySize(memorySize)
                                                            .environment(Map.of())
                                                            .code(assetCode)
                                                            .runtime(JAVA_17)
                                                            .handler(LAMBDA_HANDLER)
                                                            .logRetention(RetentionDays.ONE_DAY)
                                                            .architecture(X86_64)
                                                            .build());
        CfnFunction cfnFunction = (CfnFunction) function.getNode().getDefaultChild();
        cfnFunction.setSnapStart(CfnFunction.SnapStartProperty.builder()
                                                              .applyOn("PublishedVersions")
                                                              .build());
        Version snapstartVersion = new Version(this,
                                               LAMBDA_FUNCTION_ID + "-snap",
                                               VersionProps.builder()
                                                           .lambda(function)
                                                           .description("Snapstart Version")
                                                           .build());

        String apiId = LAMBDA_FUNCTION_ID + "-api";
        HttpApi api = new HttpApi(this, apiId, HttpApiProps.builder()
                                                           .apiName(apiId)
                                                           .description("Public API for %s".formatted(LAMBDA_FUNCTION_ID))
                                                           .build());
        HttpLambdaIntegration integration = new HttpLambdaIntegration(LAMBDA_FUNCTION_ID + "-integration",
                                                                      snapstartVersion,
                                                                      HttpLambdaIntegrationProps.builder()
                                                                                                .payloadFormatVersion(
                                                                                                        VERSION_2_0)
                                                                                                .build());
        HttpRoute build = HttpRoute.Builder.create(this, LAMBDA_FUNCTION_ID + "-route")
                                           .routeKey(HttpRouteKey.with("/" + LAMBDA_FUNCTION_ID, HttpMethod.GET))
                                           .httpApi(api)
                                           .integration(integration)
                                           .build();

        String outputId = "API_URL";
        CfnOutput.Builder.create(this, outputId)
                         .description("%s invoke URL".formatted(LAMBDA_FUNCTION_ID))
                         .value(build.getHttpApi().getApiEndpoint() + build.getPath())
                         .build();
        log.info("* * * * * API Invoke Path is an AWS Cloudformation Stack output for {}:{}",
                 getStackName(),
                 outputId);
    }
}
