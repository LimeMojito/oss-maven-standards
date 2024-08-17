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

package com.limemojito.aws.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limemojito.json.spring.LimeJacksonJsonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Configuration to create a SQS sender that uses all the standard attributes.
 */
@Configuration
@Import(LimeJacksonJsonConfiguration.class)
// avoid component scans here as the localstack configuration is present.
public class SqsSenderConfig {

    /**
     * Creates a new instance of {@link SqsSender} using the provided {@link SqsClient} and {@link ObjectMapper}.
     * This method is used to configure a SQS sender that uses all the standard attributes.
     *
     * @param sqs          the {@link SqsClient} used to interact with Amazon Simple Queue Service (SQS)
     * @param objectMapper the {@link ObjectMapper} used to convert objects to JSON
     * @return a new instance of {@link SqsSender} configured with the provided {@link SqsClient} and {@link ObjectMapper}
     */
    @Bean
    public SqsSender sqsSender(SqsClient sqs, ObjectMapper objectMapper) {
        return new SqsSender(sqs, objectMapper);
    }
}
