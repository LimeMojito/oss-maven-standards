/*
 * Copyright 2011-2025 Lime Mojito Pty Ltd
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
import com.limemojito.json.JsonLoader;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.springframework.http.HttpStatus.OK;

/**
 * A decorator for APIGatewayV2HTTPResponse that converts the output of a function into an APIGatewayV2HTTPResponse.
 * <p>
 * This decorator manages the conversion of spring-web style exceptions being thrown as RuntimeExceptions
 * by Cloud Function implementations.  ConstraintViolations are also handled automatically and converted to 400 Bad
 * Request responses.  { @code @ResponseStatus } style exceptions will be mapped correctly to Api Gateway responses
 * using the basic Lambda V2 Integration.
 * </p>
 *
 * @param <Input> the type of the input to the decorator.
 * @see ResponseStatus
 * @see ConstraintViolationException
 */
@RequiredArgsConstructor
@Slf4j
public class ApiGatewayResponseDecorator<Input> implements Function<Input, APIGatewayV2HTTPResponse> {
    /**
     * The default content type used for requests and responses.
     * The value of this constant is "application/json".
     */
    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    private final ApiGatewayExceptionMapper exceptionMapper;
    private final JsonLoader json;
    private final String contentType;
    private final Function<Input, ?> next;

    /**
     * Applies the next function in the pipeline to the given input and returns the result.
     *
     * @param input the input to be processed by the next function
     * @return the result of applying the next function to the input;
     * if the output is an instance of APIGatewayV2HTTPResponse, it is returned as is;
     * otherwise, the output is transformed into an APIGatewayV2HTTPResponse using the rebuildOutputJson method
     */
    @Override
    public APIGatewayV2HTTPResponse apply(Input input) {
        try {
            Object output = next.apply(input);
            if (output instanceof APIGatewayV2HTTPResponse) {
                log.debug("Decorated function returned APIGatewayV2HTTPResponse");
                return (APIGatewayV2HTTPResponse) output;
            } else {
                return rebuildOutputJson(output);
            }
        } catch (Throwable e) {
            log.error("Building failure response for {} {}", e.getClass().getSimpleName(), e.getMessage(), e);
            return create(DEFAULT_CONTENT_TYPE, false, newError(json, e), exceptionMapper.map(e));
        }
    }

    /**
     * Annotation aware converter than will obey annotation reason over message.  Reverts to
     * simple class name if message is null on throwable.
     *
     * @param e Throwable to determine message for.
     * @return reason message for exception.
     * @see ResponseStatus
     * @see Throwable#getMessage()
     * @see Class#getSimpleName()
     */
    public static String messageFor(Throwable e) {
        final ResponseStatus responseStatusType = e.getClass().getAnnotation(ResponseStatus.class);
        if (responseStatusType != null && !isBlank(responseStatusType.reason())) {
            return responseStatusType.reason();
        }
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }

    /**
     * Writes the given function output as bytes using Object Output Stream.
     *
     * @param functionOutput the function output to be written as bytes
     * @return the byte array representation of the function output
     * @throws IOException if an I/O error occurs while writing the bytes
     * @see ObjectOutputStream
     */
    static byte[] writeDataAsBytes(Object functionOutput) throws IOException {
        final int bufferSize = 512;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bufferSize);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(functionOutput);
            objectOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    private static String newError(JsonLoader mapper, Throwable e) {
        return mapper.toJson(new TreeMap<>(Map.of("errorMessage", messageFor(e),
                                                  "errorType", e.getClass().getName())));
    }

    private APIGatewayV2HTTPResponse rebuildOutputJson(Object functionOutput) throws IOException {
        log.debug("Map output to APIGatewayV2HTTPResponse");
        String body;
        boolean isBase64Encoded;
        if (contentType.toLowerCase().contains(DEFAULT_CONTENT_TYPE)) {
            body = json.toJson(functionOutput);
            isBase64Encoded = false;
        } else {
            byte[] data = writeDataAsBytes(functionOutput);
            body = Base64.getEncoder().encodeToString(data);
            isBase64Encoded = true;
        }
        APIGatewayV2HTTPResponse response = create(contentType, isBase64Encoded, body, OK);
        log.debug("lime:aws-lambda api response: {}", response);
        return response;
    }

    private static APIGatewayV2HTTPResponse create(String contentType, boolean isBase64Encoded, String body,
                                                   HttpStatus status) {
        return new APIGatewayV2HTTPResponse(status.value(),
                                            Map.of("content-type", contentType),
                                            emptyMap(),
                                            emptyList(),
                                            body,
                                            isBase64Encoded);
    }
}
