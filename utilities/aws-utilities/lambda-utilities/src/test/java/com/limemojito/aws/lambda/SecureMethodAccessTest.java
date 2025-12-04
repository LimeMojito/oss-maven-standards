/*
 * Copyright (c) Lime Mojito Pty Ltd 2011-2025
 *
 * Except as otherwise permitted by the Copyright Act 1967 (Cth) (as amended from time to time) and/or any other
 * applicable copyright legislation, the material may not be reproduced in any format and in any way whatsoever
 * without the prior written consent of the copyright owner.
 */

package com.limemojito.aws.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.limemojito.aws.lambda.example.SecureTestConfiguration;
import com.limemojito.json.JsonLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Note the source event json files have been "reduced" to key attributes.
 */
@ActiveProfiles("integration-test")
@SpringBootTest(classes = {SecureTestConfiguration.class})
public class SecureMethodAccessTest {

    @Autowired
    private JsonLoader json;

    @Autowired
    private Function<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> publicMethod;

    @Autowired
    private Function<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> secureMethod;


    @Test
    public void shouldInvokeNoSecurityOk() {
        APIGatewayV2HTTPEvent event = loadEvent("/event/httpEvent.json");

        APIGatewayV2HTTPResponse response = publicMethod.apply(event);

        assertAccessOk(response);
    }

    @Test
    public void shouldInvokePublicButWithGroupOk() {
        APIGatewayV2HTTPEvent event = loadEvent("/event/httpEventWithGroup.json");

        APIGatewayV2HTTPResponse response = publicMethod.apply(event);

        assertAccessOk(response);
    }

    @Test
    public void shouldInvokeWithNoGroupsAndFailOk() {
        APIGatewayV2HTTPEvent event = loadEvent("/event/httpEvent.json");

        APIGatewayV2HTTPResponse response = secureMethod.apply(event);

        assertAccessDenied(response);
    }


    @Test
    public void shouldWorkWithGroupsOk() {
        APIGatewayV2HTTPEvent event = loadEvent("/event/httpEventWithGroup.json");

        APIGatewayV2HTTPResponse response = secureMethod.apply(event);

        assertAccessOk(response);
    }

    @Test
    public void shouldInvokeWithWrongGroupAndFailOk() throws Exception {
        APIGatewayV2HTTPEvent event = loadEvent("/event/httpEventWithWrongGroup.json");

        APIGatewayV2HTTPResponse response = secureMethod.apply(event);

        assertAccessDenied(response);
    }

    private APIGatewayV2HTTPEvent loadEvent(String resourcePath) {
        return json.loadFrom(resourcePath, APIGatewayV2HTTPEvent.class);
    }

    private static void assertAccessOk(APIGatewayV2HTTPResponse response) {
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    private void assertAccessDenied(APIGatewayV2HTTPResponse response) {
        assertThat(response.getStatusCode()).isEqualTo(403);
        Map<String, Object> errorObject = json.convertToMap(response.getBody());
        assertThat(errorObject.get("errorMessage")).isEqualTo("Access Denied");
        assertThat(errorObject.get("errorType")).isEqualTo(
                "org.springframework.security.authorization.AuthorizationDeniedException");
    }

}
