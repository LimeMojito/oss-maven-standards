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
import com.amazonaws.client.builder.AwsClientBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalstackEndpointWrapper {
    private final String awsRegion;
    private final AWSCredentialsProvider providerChain;

    public LocalstackEndpointWrapper(String awsRegion, AWSCredentialsProvider providerChain) {
        this.awsRegion = awsRegion;
        this.providerChain = providerChain;
    }

    public <T extends AwsClientBuilder<?, ?>> T wrap(T standard, String endpointUrl) {
        log.info("Configuring Localstack Aws client with endpoint {}, region {} and static credentials",
                 endpointUrl,
                 awsRegion);
        standard.setCredentials(providerChain);
        standard.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpointUrl, awsRegion));
        return standard;
    }
}
