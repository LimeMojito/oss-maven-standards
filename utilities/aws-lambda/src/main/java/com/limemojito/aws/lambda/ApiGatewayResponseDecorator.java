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

package com.limemojito.aws.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@Slf4j
public class ApiGatewayResponseDecorator<Input> implements Function<Input, APIGatewayV2HTTPResponse> {
    public static final String DEFAULT_CONTENT_TYPE = "application/json";
    private final ObjectMapper mapper;
    private final String contentType;
    private final Function<Input, ?> next;

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
            return create(DEFAULT_CONTENT_TYPE, false, newError(mapper, e), statusFor(e));
        }
    }

    /**
     * INTERNAL_SERVER_ERROR by default.
     * Or @ResponseStatus if he annotation is present on the exception
     * 400 for ConstraintViolationException
     *
     * @param e Throwable to convert
     * @return an http error code.
     * @see ConstraintViolationException
     * @see ResponseStatus
     */
    public static HttpStatus statusFor(Throwable e) {
        final ResponseStatus responseStatusType = e.getClass().getAnnotation(ResponseStatus.class);
        if (responseStatusType != null) {
            // these can be set to different values.  We pay attention if not the default (500).
            if (responseStatusType.code() != INTERNAL_SERVER_ERROR) {
                return responseStatusType.code();
            }
            if (responseStatusType.value() != INTERNAL_SERVER_ERROR) {
                return responseStatusType.value();
            } else {
                return INTERNAL_SERVER_ERROR;
            }
        } else {
            if (e instanceof ConstraintViolationException) {
                return BAD_REQUEST;
            }
            return INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Annotation aware converter than will obey annotation reason over message.  Reverts to
     * simple class name if message is null on throwable.
     *
     * @param e Throwable to determine message for.
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

    @SneakyThrows
    private static String newError(ObjectMapper mapper, Throwable e) {
        return mapper.writeValueAsString(new TreeMap<>(Map.of("errorMessage", messageFor(e),
                                                              "errorType", e.getClass().getName())));
    }

    private APIGatewayV2HTTPResponse rebuildOutputJson(Object functionOutput) throws IOException {
        log.debug("Map output to APIGatewayV2HTTPResponse");
        String body;
        boolean isBase64Encoded;
        if (contentType.toLowerCase().contains(DEFAULT_CONTENT_TYPE)) {
            body = mapper.writeValueAsString(functionOutput);
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

    static byte[] writeDataAsBytes(Object functionOutput) throws IOException {
        final int bufferSize = 512;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bufferSize);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(functionOutput);
            objectOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
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
