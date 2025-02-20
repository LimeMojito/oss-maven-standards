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

package com.limemojito.aws.lambda.security;

import com.limemojito.aws.lambda.ApiGatewayExceptionMapper;
import com.limemojito.aws.lambda.ApiGatewayResponseDecoratorFactory;
import com.limemojito.json.spring.LimeJacksonJsonConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Enables method level security for AWS lambdas.  API Gateway Event request contexts are used to extract
 * principals, roles, etc.
 *
 * @see ApiGatewayAuthenticationMapper
 */
@EnableMethodSecurity
@Configuration
@Import({LimeJacksonJsonConfiguration.class, ApiGatewayResponseDecoratorFactory.class})
@ComponentScan(basePackageClasses = ApiGatewayAuthenticationMapper.class)
@Slf4j
public class AwsCloudFunctionSpringSecurityConfiguration {

    /**
     * Exception mapper capable of converting @ResponseStatus annotated exceptions, security exceptions and
     * general failures to API Gateway HTTP compatible error codes.  May be overridden with your own bean definition.
     *
     * @return the mapper.
     */
    @ConditionalOnMissingBean({ApiGatewayExceptionMapper.class})
    @Bean
    public ApiGatewayExceptionMapper defaultApiGatewayExceptionMapper() {
        log.info("Using default APIGatewayExceptionMapper");
        return new ApiGatewayExceptionMapper() {
        };
    }
}
