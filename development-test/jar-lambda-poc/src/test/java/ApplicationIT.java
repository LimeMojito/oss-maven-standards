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

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.limemojito.aws.lambda.LimeAwsLambdaConfiguration;
import com.limemojito.test.jackson.JacksonSupport;
import com.limemojito.test.lambda.LambdaSupport;
import com.limemojito.test.lambda.LambdaSupport.Lambda;
import com.limemojito.test.lambda.LambdaSupportConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.awssdk.services.lambda.model.State.ACTIVE;


@ActiveProfiles("integration-test")
@SpringBootTest(classes = LambdaSupportConfig.class)
public class ApplicationIT {

    @Autowired
    private LambdaSupport lambdaSupport;

    @Autowired
    private JacksonSupport json;

    private static Lambda LAMBDA;

    @BeforeEach
    public void setUp() {
        if (LAMBDA == null) {
            final Map<String, String> environment = Map.of(
                    "SPRING_PROFILES_ACTIVE", "integration-test"
                    /*
                    Required if more than one bean.

                    "SPRING_CLOUD_FUNCTION_DEFINITION","get"
                    */
            );
            LAMBDA = lambdaSupport.java("../jar-lambda-poc",
                                        LimeAwsLambdaConfiguration.LAMBDA_HANDLER,
                                        environment);

        }
    }


    @Test
    public void shouldDeployOk() {
        lambdaSupport.waitForState(LAMBDA, ACTIVE);
    }

    @Test
    public void shouldCallTransactionGetOkApiGatewayEvent() {
        perform("/events/getApiEvent.json");
    }

    @Test
    public void shouldCallTransactionPostOkApiGatewayEvent() {
        perform("/events/postApiEvent.json");
    }

    private void perform(String pathResource) {
        final APIGatewayV2HTTPEvent event = json.loadLambdaEvent(pathResource,
                                                                 APIGatewayV2HTTPEvent.class);

        final APIGatewayV2HTTPResponse response = lambdaSupport.invokeLambdaEvent(LAMBDA,
                                                                                  event,
                                                                                  APIGatewayV2HTTPResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(200);
        String output = json.parse(response.getBody(), String.class);
        assertThat(output).isEqualTo("world");
    }
}
