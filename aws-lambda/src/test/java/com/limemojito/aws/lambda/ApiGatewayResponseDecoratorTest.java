/*
 * Copyright  2011-2024 Lime Mojito Pty Ltd
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

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.function.Function;

import static com.limemojito.aws.lambda.ApiGatewayResponseDecorator.writeDataAsBytes;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ApiGatewayResponseDecoratorTest {

    private static final String JSON = "application/json";
    private final ObjectMapper mapper = new ObjectMapper();
    private final ApiGatewayResponseDecoratorFactory factory = new ApiGatewayResponseDecoratorFactory(mapper);
    private final TypeReference<Map<String, Object>> jsonMap = new TypeReference<>() {
    };
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldCreateFromFactory() throws IOException {
        Function<Object, APIGatewayV2HTTPResponse> responseFunction = factory.create(object -> "hello");

        APIGatewayV2HTTPResponse apiGateway = responseFunction.apply("anything");

        assertResponse(apiGateway, "\"hello\"", 200);
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

        assertResponse(apiGateway,
                       "{\"errorMessage\":\"Bang\",\"errorType\":\"java.lang.RuntimeException\"}",
                       500);
    }

    @Test
    public void shouldReturnGatewayErrorJsonResponseForValueException() throws Exception {
        Map<String, Object> json = performFunction(input -> {
            throw new NotFoundException();
        });

        assertThat(json).containsAllEntriesOf(Map.of(
                "statusCode", 404,
                "headers", Map.of("content-type", "application/json"),
                "isBase64Encoded", false
        ));
        assertBodyJson(json, Map.of(
                "errorMessage", "I am not found, so I am lost",
                "errorType", "com.limemojito.aws.lambda.ApiGatewayResponseDecoratorTest$NotFoundException"
        ));
    }

    @Test
    public void shouldReturnGatewayErrorJsonResponseForCodeException() throws Exception {
        Map<String, Object> json = performFunction(input -> {
            throw new TeapotException();
        });
        assertThat(json).containsAllEntriesOf(Map.of(
                "statusCode",
                418,
                "headers",
                Map.of("content-type", "application/json"),
                "isBase64Encoded",
                false
        ));
        assertBodyJson(json, Map.of(
                "errorMessage", "custom reason",
                "errorType", "com.limemojito.aws.lambda.ApiGatewayResponseDecoratorTest$TeapotException"
        ));
    }

    @Test
    public void shouldReturnGatewayErrorJsonResponseForRawException() throws Exception {
        Map<String, Object> json = performFunction(input -> {
            throw new RawException();
        });
        assertThat(json).containsAllEntriesOf(Map.of(
                "statusCode",
                500,
                "headers",
                Map.of("content-type", "application/json"),
                "isBase64Encoded",
                false
        ));
        assertBodyJson(json, Map.of(
                "errorMessage", "RawException",
                "errorType", "com.limemojito.aws.lambda.ApiGatewayResponseDecoratorTest$RawException"
        ));
    }

    @Test
    public void shouldReturnGatewayErrorJsonResponseForConstraintViolation() throws Exception {
        Map<String, Object> json = performFunction(input -> {
            ValidationObject object = new ValidationObject();
            object.setProperty("t");
            throw new ConstraintViolationException(validator.validate(object));
        });
        assertThat(json).containsAllEntriesOf(Map.of(
                "statusCode",
                400,
                "headers",
                Map.of("content-type", "application/json"),
                "isBase64Encoded",
                false
        ));
        assertBodyJson(json, Map.of(
                "errorMessage", "property: size must be between 5 and 10",
                "errorType", "jakarta.validation.ConstraintViolationException"
        ));
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

    @ResponseStatus(code = HttpStatus.I_AM_A_TEAPOT, reason = "custom reason")
    public static class TeapotException extends RuntimeException {
    }

    private Map<String, Object> performFunction(Function<String, ?> function) {
        Function<String, APIGatewayV2HTTPResponse> apiFunction = factory.create(function);
        APIGatewayV2HTTPResponse apiResponse = apiFunction.apply("hi there how are you");
        return mapper.convertValue(apiResponse, jsonMap);
    }

    private Map<String, Object> jsonToMap(String result) throws JsonProcessingException {
        return mapper.readValue(result, jsonMap);
    }

    private void assertBodyJson(Map<String, Object> json, Map<String, String> expectedValues) throws
                                                                                              JsonProcessingException {
        Map<String, Object> values = jsonToMap(json.get("body").toString());
        assertThat(values).containsAllEntriesOf(expectedValues);
    }

    private static void assertResponse(APIGatewayV2HTTPResponse apiGateway,
                                       String expectedBody,
                                       int successCode) throws IOException {
        assertResponse(apiGateway, JSON, expectedBody, false, successCode);
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
}
