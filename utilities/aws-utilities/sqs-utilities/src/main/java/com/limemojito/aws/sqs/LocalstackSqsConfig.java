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

package com.limemojito.aws.sqs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configures the AWS config for localstack for integration testing purposes.
 * Should be imported with your application spring configuration.
 */
@Profile("integration-test")
@Configuration
@Slf4j
public class LocalstackSqsConfig {

    private static final String FIFO = ".fifo";

    /**
     * Initializes and configures an Amazon Simple Queue Service (SQS) client.
     *
     * @param localStackSqsUrl The URL of the local Amazon SQS endpoint.
     * @param queueNameList    A list of queue names to be created.
     * @return A configured instance of the Amazon SQS client.
     */
    @Primary
    @Bean(destroyMethod = "close")
    public SqsClient sqsClient(@Value("${localstack.url}") URI localStackSqsUrl,
                               @Value("#{'${localstack.sqs.queues:}'.split(',')}") List<String> queueNameList) {
        SqsClient sqs = SqsClient.builder().endpointOverride(localStackSqsUrl).build();
        log.info("Queues to create: {}", queueNameList);
        queueNameList.stream()
                     .filter(StringUtils::hasLength)
                     .map(String::strip)
                     .forEach(name -> createQueue(sqs, name));
        return sqs;
    }

    /**
     * Creates a new Amazon Simple Queue Service (SQS) queue with the specified name and an associated dead letter Q.
     *
     * @param sqs   the Amazon SQS client used to interact with the SQS service
     * @param qName the name of the queue to be created.  Dead letter q is qName + -dlq
     * @return a CreateQueueResponse object representing the result of the operation
     */
    public static CreateQueueResponse createQueue(SqsClient sqs, String qName) {
        return createQueue(sqs, qName, true);
    }

    /**
     * Creates a new Amazon Simple Queue Service (SQS) queue with the specified name and an optional associated dead letter Q.
     *
     * @param sqs            the Amazon SQS client used to interact with the SQS service
     * @param qName          the name of the queue to be created.  Dead letter q is qName + -dlq
     * @param withDeadLetter true to create a dead letter Q
     * @return a CreateQueueResponse object representing the result of the operation
     */
    public static CreateQueueResponse createQueue(SqsClient sqs, String qName, boolean withDeadLetter) {
        if (withDeadLetter) {
            return createQ(sqs, qName, computeDlqName(qName));
        }
        return createQ(sqs, qName);
    }

    private static CreateQueueResponse createQ(SqsClient sqs, String qName, String dlQName) {
        log.info("Creating queue {} with DLQ {}", qName, dlQName);
        final String qARN = "QueueArn";
        CreateQueueResponse q = createQ(sqs, dlQName);
        final String dlqArn = sqs.getQueueAttributes(req -> req.queueUrl(q.queueUrl()).attributeNamesWithStrings(qARN))
                                 .attributesAsStrings()
                                 .get(qARN);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("RedrivePolicy", "{\"deadLetterTargetArn\":\"" + dlqArn + "\",\"maxReceiveCount\":\"1\"}");
        return createQ(sqs, qName, attributes);
    }

    private static CreateQueueResponse createQ(SqsClient sqs, String qName) {
        return createQ(sqs, qName, new HashMap<>());
    }

    private static CreateQueueResponse createQ(SqsClient sqs, String qName, Map<String, String> queueAttributes) {
        queueAttributes.put("ReceiveMessageWaitTimeSeconds", "3");
        if (qName.endsWith(FIFO)) {
            log.trace("Creating FIFO queue");
            queueAttributes.put("FifoQueue", "true");
            queueAttributes.put("ContentBasedDeduplication", "true");
        }
        final CreateQueueResponse result = sqs.createQueue(req -> req.queueName(qName)
                                                                     .attributesWithStrings(queueAttributes));
        log.info("Q {} created with url {} and attributes {}", qName, result.queueUrl(), queueAttributes);
        return result;
    }

    private static String computeDlqName(String qName) {
        return qName.endsWith(FIFO)
               ? qName.substring(0, qName.length() - FIFO.length()) + "-dlq.fifo"
               : qName + "-dlq";
    }
}
