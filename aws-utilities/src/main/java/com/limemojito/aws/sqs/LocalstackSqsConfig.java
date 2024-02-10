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

package com.limemojito.aws.sqs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configures the AWS config for localstack for integration testing purposes.  Should be imported with your application spring configuration.
 */
@Profile("integration-test")
@Configuration
@Slf4j
public class LocalstackSqsConfig {

    @Primary
    @Bean(destroyMethod = "close")
    public SqsAsyncClient sqsClient(@Value("${localstack.sqs.url}") URI localStackSqsUrl,
                                    @Value("#{'${localstack.sqs.queues:}'.split(',')}") List<String> queueNameList) {
        SqsAsyncClient sqs = SqsAsyncClient.builder().endpointOverride(localStackSqsUrl).build();
        log.info("Queues to create: {}", queueNameList);
        queueNameList.stream()
                     .filter(StringUtils::hasLength)
                     .map(String::strip)
                     .forEach(name -> createQueue(sqs, name));
        return sqs;
    }

    public static CreateQueueResponse createQueue(SqsAsyncClient sqs, String qName) {
        return createQueue(sqs, qName, true);
    }

    public static CreateQueueResponse createQueue(SqsAsyncClient sqs, String qName, boolean withDeadLetter) {
        if (withDeadLetter) {
            return createQ(sqs, qName, computeDlqName(qName));
        }
        return createQ(sqs, qName);
    }

    private static CreateQueueResponse createQ(SqsAsyncClient sqs, String qName, String dlQName) {
        log.info("Creating queue {} with DLQ {}", qName, dlQName);
        final String qARN = "QueueArn";
        CreateQueueResponse q = createQ(sqs, dlQName);
        final String dlqArn = sqs.getQueueAttributes(req -> req.queueUrl(q.queueUrl()).attributeNamesWithStrings(qARN))
                                 .join()
                                 .attributesAsStrings()
                                 .get(qARN);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("RedrivePolicy", "{\"deadLetterTargetArn\":\"" + dlqArn + "\",\"maxReceiveCount\":\"1\"}");
        return createQ(sqs, qName, attributes);
    }

    private static CreateQueueResponse createQ(SqsAsyncClient sqs, String qName) {
        return createQ(sqs, qName, new HashMap<>());
    }

    private static CreateQueueResponse createQ(SqsAsyncClient sqs, String qName, Map<String, String> queueAttributes) {
        queueAttributes.put("ReceiveMessageWaitTimeSeconds", "3");
        if (qName.endsWith(".fifo")) {
            log.trace("Creating FIFO queue");
            queueAttributes.put("FifoQueue", "true");
            queueAttributes.put("ContentBasedDeduplication", "true");
        }
        final CreateQueueResponse result = sqs.createQueue(req -> req.queueName(qName)
                                                                     .attributesWithStrings(queueAttributes))
                                              .join();
        log.info("Q {} created with url {} and attributes {}", qName, result.queueUrl(), queueAttributes);
        return result;
    }

    private static String computeDlqName(String qName) {
        final String fifoSuffix = ".fifo";
        return qName.endsWith(fifoSuffix) ? qName.substring(0,
                                                            qName.length() - fifoSuffix.length()) + "-dlq.fifo" : qName + "-dlq";
    }
}
