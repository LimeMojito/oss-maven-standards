/*
 * Copyright (c) Lime Mojito Pty Ltd 2011-2024
 *
 * Except as otherwise permitted by the Copyright Act 1967 (Cth) (as amended from time to time) and/or any other
 * applicable copyright legislation, the material may not be reproduced in any format and in any way whatsoever
 * without the prior written consent of the copyright owner.
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
    public void shouldCallTransactionPostOk() {
        final APIGatewayV2HTTPEvent event = json.loadLambdaEvent("/events/apiEvent.json",
                                                                 APIGatewayV2HTTPEvent.class);

        final APIGatewayV2HTTPResponse response = lambdaSupport.invokeLambdaEvent(LAMBDA,
                                                                                  event,
                                                                                  APIGatewayV2HTTPResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(200);
        String output = json.parse(response.getBody(), String.class);
        assertThat(output).isEqualTo("world");
    }
}
