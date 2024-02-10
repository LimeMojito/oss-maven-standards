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

package com.limemojito.aws.endpoint;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("integration-test")
@Configuration
@EnableAutoConfiguration
@Slf4j
public class LocalstackEndpointWrapperConfig {

    @Bean
    @Primary
    public AWSCredentialsProvider localstackProviderChain() {
        log.warn("Creating a fixed credentials provider for localstack");
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials("localstack", "localstack"));
    }

    @Bean
    public LocalstackEndpointWrapper localstackCredentialWrapper(AWSCredentialsProvider providerChain,
                                                                 @Value("${cloud.aws.region.static}") String awsRegion) {
        log.warn("Creating a fixed credentials endpoint wrapper for region {}", awsRegion);
        return new LocalstackEndpointWrapper(awsRegion, providerChain);
    }

}
