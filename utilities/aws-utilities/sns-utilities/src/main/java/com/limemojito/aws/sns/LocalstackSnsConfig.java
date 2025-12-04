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
 * Spring configuration for using AWS SNS against a LocalStack endpoint during integration tests.
 * <p>
 * This configuration is activated only when the {@code integration-test} Spring profile is enabled.
 * It exposes a primary {@link SnsClient} bean pointing to the configured LocalStack URL and
 * optionally creates SNS topics (including FIFO topics) at startup to simplify test setup.
 * </p>
 *
 * # Configuration properties
 * <ul>
 *   <li>{@code localstack.url} – the LocalStack endpoint to use for SNS (for example, {@code http://localhost:4566}).</li>
 *   <li>{@code localstack.sns.topics} – a comma-separated list of topic names to create on startup.
 *       Names ending with {@code .fifo} are created as FIFO topics with content-based deduplication enabled.</li>
 * </ul>
 *
 * <p>Import this class from your test application configuration to provision the SNS client and
 * any required topics before tests execute.</p>
 */
@Profile("integration-test")
@Configuration
@Slf4j
public class LocalstackSnsConfig {

    /**
     * Creates the primary {@link SnsClient} bean configured to talk to LocalStack and
     * provisions any topics specified via {@code localstack.sns.topics}.
     *
     * @param localStackSnsUrl the LocalStack SNS endpoint URL (property: {@code localstack.url})
     * @param topicList        optional list of topic names (comma-separated) to create on startup;
     *                         names ending in {@code .fifo} are created as FIFO topics
     * @return a configured {@link SnsClient}; caller is not required to close it (Spring manages lifecycle)
     */
    @Primary
    @Bean(destroyMethod = "close")
    public SnsClient amazonSNSAsync(@Value("${localstack.url}") URI localStackSnsUrl,
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

    /**
     * Creates an SNS topic with the given {@code name}. If the name ends with {@code .fifo},
     * the topic is created as a FIFO topic with content-based deduplication enabled.
     *
     * @param sns  the SNS client to use
     * @param name the topic name to create (append {@code .fifo} to create a FIFO topic)
     * @return the {@link CreateTopicResponse} containing the created topic's ARN
     */
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
