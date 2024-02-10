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

package com.limemojito.test.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.limemojito.aws.sqs.LocalstackSqsConfig;
import io.awspring.cloud.sqs.operations.SqsSendOptions;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class SqsSupport {
    public static final String HEADER_MESSAGE_DEDUPLICATION_ID = "message-deduplication-id";
    public static final String HEADER_MESSAGE_GROUP_ID = "message-group-id";
    public static final int SHORT_POLL = 5;
    public static final int MEDIUM_POLL = 10;
    public static final int MAX_POLL = 20;
    private final SqsAsyncClient sqs;
    private final SqsTemplate template;
    private final ObjectMapper objectMapper;

    @Autowired
    public SqsSupport(SqsAsyncClient sqs, ObjectMapper objectMapper) {
        this.sqs = sqs;
        this.template = SqsTemplate.newTemplate(sqs);
        this.objectMapper = objectMapper;
    }

    public int getQueueCount(String queueName) {
        final String key = "ApproximateNumberOfMessages";
        final GetQueueAttributesResponse result = sqs.getQueueAttributes(req -> req.queueUrl(queueName)
                                                                                   .attributeNamesWithStrings(List.of(
                                                                                           key)))
                                                     .join();
        final String value = result.attributesAsStrings().getOrDefault(key, "0");
        return Integer.parseInt(value);
    }

    public boolean exists(String queueName) {
        try {
            getQueueUrl(queueName);
            return true;
        } catch (QueueDoesNotExistException e) {
            return false;
        }
    }

    public String create(String queueName) {
        return LocalstackSqsConfig.createQueue(sqs, queueName).queueUrl();
    }

    public String getQueueUrl(String queueName) {
        return sqs.getQueueUrl(req -> req.queueName(queueName)).join().queueUrl();
    }

    public void send(String name, String body) {
        convertAndSend(name, body);
    }

    public void convertAndSend(String qName, Object message) {
        convertAndSend(qName, message, null);
    }

    public void convertAndSend(String qName, Object message, Map<String, Object> headers) {
        template.send(to -> {
            SqsSendOptions<Object> options = to.queue(getQueueUrl(qName))
                                               .payload(message);
            if (qName.endsWith("fifo") && headers != null) {
                to.headers(headers);
                for (String key : headers.keySet()) {
                    final Object value = headers.get(key);
                    if (key.equals(HEADER_MESSAGE_DEDUPLICATION_ID)) {
                        options.messageDeduplicationId(value.toString());
                    } else if (key.equals(HEADER_MESSAGE_GROUP_ID)) {
                        options.messageGroupId(value.toString());
                    }
                }
            }
        });
        log.info("Sent message: {} {} to {}", message, headers, qName);
    }

    public Message waitForMessage(String queueName) throws TimeoutException {
        return waitForMessages(queueName, MEDIUM_POLL).get(0);
    }

    public Message waitForMessage(String queueName, int waitTimeSeconds) throws TimeoutException {
        return waitForMessages(queueName, waitTimeSeconds).get(0);
    }

    public List<Message> waitForMessages(String queueName, int waitTimeSeconds) throws TimeoutException {
        if (waitTimeSeconds > MAX_POLL) {
            throw new IllegalArgumentException(format("Max wait time for SQS poll is %d seconds", MAX_POLL));
        }
        final String queueUrl = getQueueUrl(queueName);
        final ReceiveMessageResponse result = sqs.receiveMessage(req -> req.queueUrl(queueUrl)
                                                                           .waitTimeSeconds(waitTimeSeconds))
                                                 .join();
        final List<Message> messages = result.messages();
        if (messages.isEmpty()) {
            throw new TimeoutException("Gave up waiting for message on " + queueName);
        } else {
            // acknowledge messages
            messages.forEach(m -> sqs.deleteMessage(req -> req.queueUrl(queueUrl).receiptHandle(m.receiptHandle()))
                                     .join());
            log.info("Received {} messages", messages.size());
            return messages;
        }
    }

    public <T> List<T> waitForMessages(String queueName, Class<T> clazz) throws TimeoutException {
        return waitForMessages(queueName, MEDIUM_POLL, clazz);
    }

    public <T> List<T> waitForMessages(String queueName, TypeReference<T> typeReference) throws TimeoutException {
        return waitForMessages(queueName, MEDIUM_POLL, typeReference);
    }

    public <T> List<T> waitForMessages(String queueName, int waitTimeSeconds, Class<T> clazz) throws TimeoutException {
        return waitForMessages(queueName, waitTimeSeconds).stream()
                                                          .map(m -> toObject(m.body(), clazz))
                                                          .collect(toList());
    }

    public <T> List<T> waitForMessages(String queueName, int waitTimeSeconds, TypeReference<T> typeReference) throws
            TimeoutException {
        return waitForMessages(queueName, waitTimeSeconds).stream()
                                                          .map(m -> toObject(m.body(), typeReference))
                                                          .collect(toList());
    }

    /**
     * This method will unwrap the SNS notification message expecting an application/json body content.
     *
     * @param queueName       name of queue to wait on
     * @param waitTimeSeconds maximum seconds to wait
     * @param clazz           of expected notification message body
     * @param <T>             Type of expected notification message body
     * @return list of unwrapped notification messages
     * @throws TimeoutException if no messages are found.
     */
    public <T> List<T> waitForNotificationMessages(String queueName, int waitTimeSeconds, Class<T> clazz) throws
            TimeoutException {
        return waitForMessages(queueName, waitTimeSeconds).stream()
                                                          .map(m -> toObject(m.body(), Map.class))
                                                          .map(sns -> toObject((String) sns.get("Message"), clazz))
                                                          .collect(toList());
    }

    /**
     * This method will unwrap the SNS notification message expecting an application/json body content.
     *
     * @param queueName       name of queue to wait on
     * @param waitTimeSeconds maximum seconds to wait
     * @param type            of expected notification message body
     * @param <T>             Type of expected notification message body
     * @return list of unwrapped notification messages
     * @throws TimeoutException if no messages are found.
     */
    public <T> List<T> waitForNotificationMessages(String queueName, int waitTimeSeconds, TypeReference<T> type) throws
            TimeoutException {
        return waitForMessages(queueName, waitTimeSeconds).stream()
                                                          .map(m -> toObject(m.body(), Map.class))
                                                          .map(sns -> toObject((String) sns.get("Message"), type))
                                                          .collect(toList());
    }

    /**
     * This method will unwrap the SNS notification message expecting an application/json body content.
     *
     * @param queueName            name of queue to wait on
     * @param waitUntilTimeSeconds maximum seconds to wait
     * @param clazz                of expected notification message body
     * @param <T>                  Type of expected notification message body
     * @param messageCountMin      minimum number of messages to wait until
     * @return list of unwrapped notification messages
     * @throws TimeoutException if no messages are found.
     */
    public <T> List<T> waitUntilNotificationMessageCountGreaterThan(String queueName,
                                                                    int waitUntilTimeSeconds,
                                                                    Class<T> clazz,
                                                                    int messageCountMin) throws TimeoutException {
        return waitUntilMessageCountGreaterThan(queueName, waitUntilTimeSeconds, messageCountMin).stream()
                                                                                                 .map(m -> toObject(m.body(),
                                                                                                                    Map.class))
                                                                                                 .map(sns -> toObject((String) sns.get(
                                                                                                                              "Message"),
                                                                                                                      clazz))
                                                                                                 .collect(toList());
    }

    /**
     * This method will unwrap the SNS notification message expecting an application/json body content.
     *
     * @param queueName            name of queue to wait on
     * @param waitUntilTimeSeconds maximum seconds to wait
     * @param type                 of expected notification message body
     * @param <T>                  Type of expected notification message body
     * @param messageCountMin      minimum number of messages to wait until
     * @return list of unwrapped notification messages
     * @throws TimeoutException if no messages are found.
     */
    public <T> List<T> waitUntilNotificationMessageCountGreaterThan(String queueName,
                                                                    int waitUntilTimeSeconds,
                                                                    TypeReference<T> type,
                                                                    int messageCountMin) throws TimeoutException {
        return waitUntilMessageCountGreaterThan(queueName, waitUntilTimeSeconds, messageCountMin).stream()
                                                                                                 .map(m -> toObject(m.body(),
                                                                                                                    Map.class))
                                                                                                 .map(sns -> toObject((String) sns.get(
                                                                                                                              "Message"),
                                                                                                                      type))
                                                                                                 .collect(toList());
    }

    public List<Message> waitUntilMessageCountGreaterThan(String queueName,
                                                          int waitUntilTimeSeconds,
                                                          int messageCountMin,
                                                          int pollSeconds) throws TimeoutException {
        final long maxWaitTime = Instant.now().plusSeconds(waitUntilTimeSeconds).toEpochMilli();
        final List<Message> totalMessages = new ArrayList<>(messageCountMin);
        while (totalMessages.size() < messageCountMin && System.currentTimeMillis() < maxWaitTime) {
            totalMessages.addAll(waitForMessages(queueName, pollSeconds));
        }
        if (totalMessages.size() < messageCountMin) {
            throw new TimeoutException(format("Could not collect %d messages in %d seconds. (%d/%d)",
                                              messageCountMin,
                                              waitUntilTimeSeconds,
                                              totalMessages.size(),
                                              messageCountMin));
        }
        return totalMessages;
    }

    public List<Message> waitUntilMessageCountGreaterThan(String queueName,
                                                          int waitUntilTimeSeconds,
                                                          int messageCountMin) throws TimeoutException {
        return waitUntilMessageCountGreaterThan(queueName, waitUntilTimeSeconds, messageCountMin, SHORT_POLL);
    }


    public <T> List<T> waitUntilMessageCountGreaterThan(String queueName,
                                                        int waitUntilTimeSeconds,
                                                        int messageCountMin,
                                                        Class<T> clazz) throws TimeoutException {
        return waitUntilMessageCountGreaterThan(queueName, waitUntilTimeSeconds, messageCountMin, MEDIUM_POLL, clazz);
    }

    public <T> List<T> waitUntilMessageCountGreaterThan(String queueName,
                                                        int waitUntilTimeSeconds,
                                                        int messageCountMin,
                                                        TypeReference<T> type) throws TimeoutException {
        return waitUntilMessageCountGreaterThan(queueName, waitUntilTimeSeconds, messageCountMin, MEDIUM_POLL, type);
    }

    public <T> List<T> waitUntilMessageCountGreaterThan(String queueName,
                                                        int waitUntilTimeSeconds,
                                                        int messageCountMin,
                                                        int pollSeconds,
                                                        Class<T> clazz) throws TimeoutException {
        List<Message> found = waitUntilMessageCountGreaterThan(queueName,
                                                               waitUntilTimeSeconds,
                                                               messageCountMin,
                                                               pollSeconds);
        return found.stream()
                    .map(m -> toObject(m.body(), clazz))
                    .collect(toList());
    }

    public <T> List<T> waitUntilMessageCountGreaterThan(String queueName,
                                                        int waitUntilTimeSeconds,
                                                        int messageCountMin,
                                                        int pollSeconds,
                                                        TypeReference<T> type) throws TimeoutException {
        List<Message> found = waitUntilMessageCountGreaterThan(queueName,
                                                               waitUntilTimeSeconds,
                                                               messageCountMin,
                                                               pollSeconds);
        return found.stream()
                    .map(m -> toObject(m.body(), type))
                    .collect(toList());
    }

    public void purge(String queueName) {
        final String url = getQueueUrl(queueName);
        ReceiveMessageResponse receiveMessageResult = sqs.receiveMessage(r -> r.queueUrl(url)).join();
        int count = 0;
        while (!receiveMessageResult.messages().isEmpty()) {
            final List<Message> messages = receiveMessageResult.messages();
            for (Message message : messages) {
                sqs.deleteMessage(r -> r.queueUrl(url).receiptHandle(message.receiptHandle())).join();
                count++;
            }
            receiveMessageResult = sqs.receiveMessage(r -> r.queueUrl(url)).join();
        }
        log.info("Purged {} messages", count);
    }

    private <T> T toObject(String json, TypeReference<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert to object " + json, e);
        }
    }

    private <T> T toObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert to object " + json, e);
        }
    }

}
