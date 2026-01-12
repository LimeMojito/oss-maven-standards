/*
 * Copyright 2011-2026 Lime Mojito Pty Ltd
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

package com.limemojito.aws.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.limemojito.aws.lambda.security.ApiGatewayAuthenticationMapper;
import com.limemojito.aws.lambda.security.ApiGatewayPrincipal;
import com.limemojito.json.JsonLoader;
import com.limemojito.json.JsonMapperPrototype;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.limemojito.aws.lambda.ApiGatewayResponseDecorator.writeDataAsBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.ALREADY_REPORTED;

@Slf4j
public class ApiGatewayResponseDecoratorTest {

    private static final String JSON = "application/json";
    private final ApiGatewayResponseDecoratorFactory factory;
    private final Validator validator;
    private final JsonLoader json;

    @SuppressWarnings("resource")
    public ApiGatewayResponseDecoratorTest() {
        json = new JsonLoader(JsonMapperPrototype.buildBootLikeMapper());
        factory = new ApiGatewayResponseDecoratorFactory(json, new ApiGatewayExceptionMapper() {
        }, new ApiGatewayAuthenticationMapper("cognito:groups", "ANON", "anon", "PUBLIC"));
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldCreateFromFactory() throws IOException {
        Function<Object, APIGatewayV2HTTPResponse> responseFunction = factory.create(object -> "hello");

        APIGatewayV2HTTPResponse apiGateway = responseFunction.apply("anything");

        assertResponse(apiGateway, "\"hello\"", 200);
    }

    @FunctionalInterface
    public interface ContextTest {
        void apply(ApiGatewayContext ctx);
    }

    @Test
    public void shouldExtractUserFromEvent() {
        performContextTest("/event/httpEventWithWrongGroup.json", ctx -> {
            final ApiGatewayPrincipal principal = ctx.getPrincipal();
            assertThat(principal.getName()).isEqualTo("sub-bob@example.com");
            assertThat(principal.sub()).isEqualTo("sub-bob@example.com");
            assertThat(principal.userName()).isEqualTo("bob@example.com");
            assertThat(principal.groups()).contains("WRONG", "PEABODY", "accounting");
        });
    }

    @Test
    public void shouldExtractAuthorizationTokenFromEvent() {
        performContextTest("/event/httpEventWithWrongGroup.json",
                           ApiGatewayContext::fetchAccessToken);
    }

    @Test
    public void shouldThrowWhenMissingTokenOnFetch() {
        performContextTest("/event/httpEventAnonymous.json",
                           ApiGatewayContext::fetchAccessToken,
                           400,
                           "{\"errorMessage\":\"No access token passed in HTTP event\",\"errorType\":\"com.limemojito.aws.lambda.ApiGatewayContext$TokenNotFound\"}");
    }

    @Test
    public void shouldExtractAnonymousUserFromEvent() {
        performContextTest("/event/httpEventAnonymous.json", ctx -> {
            final ApiGatewayPrincipal principal = ctx.getPrincipal();
            assertThat(principal.userName()).isEqualTo("anon");
            assertThat(principal.groups()).contains("PUBLIC");
        });
    }

    @Test
    public void shouldFindClaimOk() {
        performContextTest("/event/httpEventWithWrongGroup.json",
                           ctx -> {
                               assertThat(ctx.fetchClaim("sub")).isEqualTo("sub-bob@example.com");
                           });
    }

    @Test
    public void shouldFindMultiValueClaimOk() {
        performContextTest("/event/httpEventWithWrongGroup.json",
                           ctx -> {
                               assertThat(ctx.fetchClaimValues("cognito:groups"))
                                       .containsExactly("WRONG",
                                                        "PEABODY",
                                                        "accounting");
                           });
    }

    @Test
    public void shouldFindMultiValueClaimAsSingleOk() {
        performContextTest("/event/httpEventWithWrongGroup.json",
                           ctx -> {
                               assertThat(ctx.fetchClaim("cognito:groups")).isEqualTo("[WRONG,PEABODY,accounting]");
                           });
    }

    @Test
    public void shouldFindSingleValueClaimAsMultiValueOk() {
        performContextTest("/event/httpEventWithWrongGroup.json",
                           ctx -> {
                               assertThat(ctx.fetchClaimValues("sub")).isEqualTo(List.of("sub-bob@example.com"));
                           });
    }

    @Test
    public void shouldNotFindClaimOk() {
        performContextTest("/event/httpEventAnonymous.json",
                           ctx -> ctx.fetchClaim("sub"),
                           400,
                           "{\"errorMessage\":\"Claim sub not found in HTTP event\",\"errorType\":\"com.limemojito.aws.lambda.ApiGatewayContext$ClaimNotFound\"}");
    }

    @Test
    public void shouldNotFindMultiValueClaimOk() {
        performContextTest("/event/httpEventAnonymous.json",
                           ctx -> ctx.fetchClaimValues("sub"),
                           400,
                           "{\"errorMessage\":\"Claim sub not found in HTTP event\",\"errorType\":\"com.limemojito.aws.lambda.ApiGatewayContext$ClaimNotFound\"}");
    }

    @Test
    public void shouldCreateFromFactoryOverrideContentType() throws Exception {
        String contentType = "application/octet-stream";
        Function<Object, APIGatewayV2HTTPResponse> responseFunction = factory.create(contentType, object -> "hello");

        APIGatewayV2HTTPResponse apiGateway = responseFunction.apply("anything");

        assertResponse(apiGateway, contentType, "hello", true, 200);
    }

    @Test
    public void shouldSkipIfAlreadyHTTPResponse() {
        APIGatewayV2HTTPResponse alreadyMade = new APIGatewayV2HTTPResponse();
        Function<Object, APIGatewayV2HTTPResponse> responseFunction = factory.create(object -> alreadyMade);

        APIGatewayV2HTTPResponse apiGateway = responseFunction.apply("anything");

        assertThat(apiGateway).isEqualTo(alreadyMade);
    }

    @Test
    public void shouldReturnGatewayResponseOnExceptionThrow() throws Exception {
        Function<Object, APIGatewayV2HTTPResponse> responseFunction = factory.create(object -> {
            throw new RuntimeException("Bang");
        });

        APIGatewayV2HTTPResponse apiGateway = responseFunction.apply("anything");

        assertResponse(apiGateway, "{\"errorMessage\":\"Bang\",\"errorType\":\"java.lang.RuntimeException\"}", 500);
    }

    @Test
    public void shouldReturnForbiddenOnSpringSecurityExceptionThrow() throws Exception {
        Function<Object, APIGatewayV2HTTPResponse> responseFunction = factory.create(object -> {
            throw new AccessDeniedException("Spring Security says NO");
        });

        APIGatewayV2HTTPResponse apiGateway = responseFunction.apply("anything");

        assertResponse(apiGateway,
                       "{\"errorMessage\":\"Spring Security says NO\",\"errorType\":\"org.springframework.security.access.AccessDeniedException\"}",
                       403);
    }

    @Test
    public void shouldReturnGatewayErrorJsonResponseForValueException() throws Exception {
        Map<String, Object> json = performFunction(input -> {
            throw new NotFoundException();
        });

        assertThat(json).containsAllEntriesOf(Map.of("statusCode",
                                                     404,
                                                     "headers",
                                                     Map.of("content-type", "application/json"),
                                                     "isBase64Encoded",
                                                     false));
        assertBodyJson(json,
                       Map.of("errorMessage",
                              "I am not found, so I am lost",
                              "errorType",
                              "com.limemojito.aws.lambda.ApiGatewayResponseDecoratorTest$NotFoundException"));
    }

    @Test
    public void shouldReturnGatewayErrorJsonResponseForCodeException() throws Exception {
        Map<String, Object> json = performFunction(input -> {
            throw new AlreadyReportedException();
        });
        assertThat(json).containsAllEntriesOf(Map.of("statusCode",
                                                     ALREADY_REPORTED.value(),
                                                     "headers",
                                                     Map.of("content-type", "application/json"),
                                                     "isBase64Encoded",
                                                     false));
        assertBodyJson(json,
                       Map.of("errorMessage",
                              "custom reason",
                              "errorType",
                              "com.limemojito.aws.lambda.ApiGatewayResponseDecoratorTest$AlreadyReportedException"));
    }

    @Test
    public void shouldReturnGatewayErrorJsonResponseForRawException() throws Exception {
        Map<String, Object> json = performFunction(input -> {
            throw new RawException();
        });
        assertThat(json).containsAllEntriesOf(Map.of("statusCode",
                                                     500,
                                                     "headers",
                                                     Map.of("content-type", "application/json"),
                                                     "isBase64Encoded",
                                                     false));
        assertBodyJson(json,
                       Map.of("errorMessage",
                              "RawException",
                              "errorType",
                              "com.limemojito.aws.lambda.ApiGatewayResponseDecoratorTest$RawException"));
    }

    @Test
    public void shouldReturnGatewayErrorJsonResponseForConstraintViolation() throws Exception {
        Map<String, Object> json = performFunction(input -> {
            ValidationObject object = new ValidationObject();
            object.setProperty("t");
            throw new ConstraintViolationException(validator.validate(object));
        });
        assertThat(json).containsAllEntriesOf(Map.of("statusCode",
                                                     400,
                                                     "headers",
                                                     Map.of("content-type", "application/json"),
                                                     "isBase64Encoded",
                                                     false));
        assertBodyJson(json,
                       Map.of("errorMessage",
                              "property: size must be between 5 and 10",
                              "errorType",
                              "jakarta.validation.ConstraintViolationException"));
    }

    @Valid
    @Data
    public static class ValidationObject {
        @Size(min = 5, max = 10)
        private String property;
    }

    public static class RawException extends RuntimeException {
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class NotFoundException extends RuntimeException {
        public NotFoundException() {
            super("I am not found, so I am lost");
        }
    }

    @ResponseStatus(code = ALREADY_REPORTED, reason = "custom reason")
    public static class AlreadyReportedException extends RuntimeException {
    }

    private Map<String, Object> performFunction(Function<String, ?> function) {
        Function<String, APIGatewayV2HTTPResponse> apiFunction = factory.create(function);
        APIGatewayV2HTTPResponse apiResponse = apiFunction.apply("hi there how are you");
        return json.convertToMap(apiResponse);
    }

    private void assertBodyJson(Map<String, Object> json, Map<String, String> expectedValues) {
        Map<String, Object> values = this.json.convertToMap(json.get("body").toString());
        assertThat(values).containsAllEntriesOf(expectedValues);
    }

    private static void assertResponse(APIGatewayV2HTTPResponse apiGateway, String expectedBody, int successCode) throws
                                                                                                                  IOException {
        assertResponse(apiGateway, JSON, expectedBody, false, successCode);
    }

    private APIGatewayV2HTTPEvent loadEvent(String resourcePath) {
        return json.loadFrom(resourcePath, APIGatewayV2HTTPEvent.class);
    }

    private static void assertResponse(APIGatewayV2HTTPResponse apiGateway,
                                       String contentType,
                                       Object expectedBody,
                                       boolean base64Encoded,
                                       int successCode) throws IOException {
        assertThat(apiGateway).isNotNull();
        assertThat(apiGateway.getStatusCode()).isEqualTo(successCode);
        assertThat(apiGateway.getHeaders()).containsEntry("content-type", contentType);
        String bodyCheck;
        if (base64Encoded) {
            final byte[] data = writeDataAsBytes(expectedBody);
            bodyCheck = Base64.getEncoder().encodeToString(data);
        } else {
            bodyCheck = expectedBody.toString();
        }
        assertThat(apiGateway.getBody()).isEqualTo(bodyCheck);
        assertThat(apiGateway.getIsBase64Encoded()).isEqualTo(base64Encoded);
    }

    private void performContextTest(String eventPath, ContextTest contextTest, int statusCode, String body) {
        Function<Object, APIGatewayV2HTTPResponse> function = factory.create((Function<Object, Object>) object -> {
            final ApiGatewayContext ctx = factory.getCurrentApiGatewayContext();
            contextTest.apply(ctx);
            return true;
        });
        APIGatewayV2HTTPEvent event = loadEvent(eventPath);
        APIGatewayV2HTTPResponse response = function.apply(event);
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
        assertThat(response.getBody()).isEqualTo(body);
    }

    private void performContextTest(String eventPath, ContextTest contextTest) {
        performContextTest(eventPath, contextTest, 200, "true");
    }
}
