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

package com.limemojito.aws.sns;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Configures the AWS config for localstack for integration testing purposes.  Should be imported with your application spring configuration.
 */
@Profile("integration-test")
@Configuration
@Slf4j
public class LocalstackSnsConfig {

    @Primary
    @Bean(destroyMethod = "close")
    public SnsClient amazonSNSAsync(@Value("${localstack.sns.url}") URI localStackSnsUrl,
                                    @Value("#{'${localstack.sns.topics:}'.split(',')}") List<String> topicList) {
        SnsClient sns = SnsClient.builder()
                                 .endpointOverride(localStackSnsUrl)
                                 .build();

        log.info("Topics to create: {}", topicList);
        topicList.stream()
                 .filter(StringUtils::hasLength)
                 .map(String::strip)
                 .forEach(name -> createTopic(sns, name));
        return sns;
    }

    @SneakyThrows
    public static CreateTopicResponse createTopic(SnsClient sns, String name) {
        log.info("Creating topic {}", name);
        final CreateTopicResponse topic;
        if (name.endsWith(".fifo")) {
            log.debug("Creating FIFO topic");
            topic = sns.createTopic(req -> req.name(name)
                                              .attributes(Map.of("FifoTopic", "true",
                                                                 "ContentBasedDeduplication", "true")));
        } else {
            topic = sns.createTopic(req -> req.name(name));
        }
        log.debug("Created topic {} with arn {}", name, topic.topicArn());
        return topic;
    }
}
