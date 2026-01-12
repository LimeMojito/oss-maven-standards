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

package com.limemojito.aws.localstack.lambda;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.lambda.LambdaClient;

import java.net.URI;

/**
 * This lives in the localstack package to avoid scan conflicts with LimeAwsLambdaConfiguration.
 *
 * @see com.limemojito.aws.lambda.LimeAwsLambdaConfiguration
 */
@Profile("integration-test")
@Configuration
public class LocalstackLambdaConfig {

    /**
     * Localstack endpoint overridden AWS client.
     *
     * @param localStackUrl URL to localstack  (localstack.url)
     * @return the lambda client.
     */
    @Primary
    @Bean(destroyMethod = "close")
    public LambdaClient lambdaClient(@Value("${localstack.url}") URI localStackUrl) {
        return LambdaClient.builder()
                           .endpointOverride(localStackUrl)
                           .build();
    }
}
