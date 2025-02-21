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

package com.limemojito.aws.lambda.example;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.limemojito.aws.lambda.ApiGatewayResponseDecoratorFactory;
import com.limemojito.aws.lambda.LimeAwsLambdaConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.function.Function;

@Configuration
@EnableMethodSecurity
@ComponentScan(basePackageClasses = BeanWithSecurityAnnotations.class)
@Import(LimeAwsLambdaConfiguration.class)
public class SecureTestConfiguration {

    @Bean
    Function<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> publicMethod(ApiGatewayResponseDecoratorFactory factory,
                                                                           BeanWithSecurityAnnotations bean) {
        return factory.create(event -> {
            bean.executePublic();
            return "OK";
        });
    }


    @Bean
    Function<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> secureMethod(ApiGatewayResponseDecoratorFactory factory,
                                                                           BeanWithSecurityAnnotations bean) {
        return factory.create(event -> {
            bean.executeSecured();
            return "OK";
        });
    }
}
