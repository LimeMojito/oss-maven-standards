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

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.limemojito.aws.lambda.security.ApiGatewayAuthenticationMapper;
import com.limemojito.json.JsonLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Generates a response decorator for a lambda function.  Should be created one for each function bean.  Note that
 * the exception mapper can be overridden in bean configuration with your own version if desired.
 *
 * @see ApiGatewayExceptionMapper
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApiGatewayResponseDecoratorFactory {
    private final JsonLoader jsonMapper;
    private final ApiGatewayExceptionMapper exceptionMapper;
    private final ApiGatewayAuthenticationMapper authenticationMapper;

    /**
     * Create a new decorator returning APIGatewayV2HttpResponse from your function output or RuntimeException.
     * Defaults to DEFAULT_CONTENT_TYPE for success response data.
     *
     * @param <Input>   Input Type
     * @param <Output>> Output Type
     * @param function  function to chain with
     * @return Output function that maps to APIGateway responses (including errors).
     * @see ApiGatewayResponseDecorator
     * @see ApiGatewayResponseDecorator#DEFAULT_CONTENT_TYPE
     * @see APIGatewayV2HTTPResponse
     */
    public <Input, Output> Function<Input, APIGatewayV2HTTPResponse> create(Function<Input, Output> function) {
        return create(ApiGatewayResponseDecorator.DEFAULT_CONTENT_TYPE, function);
    }

    /**
     * Create a new decorator returning APIGatewayV2HttpResponse from your function output or RuntimeException.
     *
     * @param <Input>     Input Type
     * @param <Output>>   Output Type
     * @param contentType contentType for success data.  Errors are always an application/json lambda event.
     * @param function    function to chain with
     * @return Output function that maps to APIGateway responses (including errors).
     * @see ApiGatewayResponseDecorator
     * @see ApiGatewayResponseDecorator#DEFAULT_CONTENT_TYPE
     * @see APIGatewayV2HTTPResponse
     */
    public <Input, Output> Function<Input, APIGatewayV2HTTPResponse> create(String contentType,
                                                                            Function<Input, Output> function) {
        return new ApiGatewayResponseDecorator<>(authenticationMapper,
                                                 exceptionMapper,
                                                 jsonMapper,
                                                 contentType,
                                                 function);
    }

    /**
     * This method can be used to retrieve the {@link com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent}
     * information in a parsed form where Spring Security is being used, or direct claims access is required.
     * <pre>
     *     factory.create(object -> {ApiGatewayContext ctx = factory.getCurrentApiGatewayContext(); ... });
     * </pre>
     * The context can be used to retire authorisation principals, claims, raw event, etc.
     *
     * @return a new context.  Never null, mau be "invalid" {@link ApiGatewayContext#isValid()};.
     */
    public ApiGatewayContext getCurrentApiGatewayContext() {
        return new ApiGatewayContext(ApiGatewayResponseDecorator.getCurrentAuthentication(),
                                     ApiGatewayResponseDecorator.getCurrentEvent());
    }
}
