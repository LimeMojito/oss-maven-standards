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

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.limemojito.aws.lambda.security.AwsCloudFunctionSpringSecurityConfiguration;
import com.limemojito.json.spring.LimeJacksonJsonConfiguration;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.cloud.function.adapter.aws.FunctionInvoker;
import org.springframework.cloud.function.context.config.ContextFunctionCatalogAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Some native hints for working with AWS lambda.  We avoid native after issue with runtimes, etc. SnapStart is preferred.
 */
@Configuration
@Import({ContextFunctionCatalogAutoConfiguration.class,
        LimeJacksonJsonConfiguration.class,
        AwsCloudFunctionSpringSecurityConfiguration.class})
@RegisterReflectionForBinding({org.joda.time.DateTime.class, APIGatewayV2HTTPEvent.class})
public class LimeAwsLambdaConfiguration {
    /**
     * Default lambda handler for spring cloud functions.  Currently, FunctionInvoker, you can use the
     * ApiGatewayResponseDecorator to pipeline error handling to a good AWS Lambda integration experience.
     *
     * @see FunctionInvoker
     * @see ApiGatewayResponseDecoratorFactory
     */
    public static final String LAMBDA_HANDLER = "%s::handleRequest".formatted(FunctionInvoker.class.getName());
}
