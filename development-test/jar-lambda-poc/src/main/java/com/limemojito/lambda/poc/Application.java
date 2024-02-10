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

package com.limemojito.lambda.poc;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.limemojito.aws.lambda.ApiGatewayResponseDecoratorFactory;
import com.limemojito.aws.lambda.LimeAwsLambdaConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.function.Function;

@SpringBootApplication
@Import(LimeAwsLambdaConfiguration.class)
@Slf4j
public class Application {

    @Bean
    public Function<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> hello(ApiGatewayResponseDecoratorFactory decoratorFactory) {
        log.info("Initialized Decorator Function");
        return decoratorFactory.create((apiEvent) -> {
            log.info("Received {}", apiEvent);
            return "world";
        });
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
