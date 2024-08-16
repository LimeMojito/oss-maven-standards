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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.limemojito.aws.sqs.LocalstackSqsConfig;
import com.limemojito.aws.sqs.SqsSender;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
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
import static software.amazon.awssdk.services.sqs.model.QueueAttributeName.QUEUE_ARN;

/**
 * This class provides support for working with Amazon Simple Queue Service (SQS). It includes methods for managing
 * queues, sending and receiving messages, and checking queue attributes.
 * <p>
 * Always adds the following SQS attributes:
 * <ul>
 *     <li>id - unique id for each message</li>
 *     <li>timestamp - epoch milliseconds</li>
 *     <li>contentType - mime type</li>
 *     <li>Content-Type - Mime like attribute</li>
 *     <li>Content-Length - Mime like attribute</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SqsSupport {
    /**
     * The SHORT_POLL constant represents the duration in seconds for a short max polling interval.
     * It is a final int variable with a value of 5.
     */
    public static final int SHORT_POLL = 5;
    /**
     * The MEDIUM_POLL constant represents the duration in seconds for a medium max polling interval.
     * It is a final int variable with a value of 10.
     */
    public static final int MEDIUM_POLL = 10;
    /**
     * The MAX_POLL constant represents the duration in seconds for a long max polling interval.
     * It is a final int variable with a value of 20.
     */
    public static final int MAX_POLL = 20;

    private final SqsClient sqs;
    private final SqsSender sqsSender;
    private final ObjectMapper objectMapper;

    /**
     * Retrieves the approximate number of messages in a specified queue.
     * This method makes use of the Amazon Simple Queue Service (SQS) to get the queue attributes,
     * specifically the "ApproximateNumberOfMessages" attribute.
     *
     * @param queueName The name of the queue for which to retrieve the message count.
     * @return The approximate number of messages in the specified queue.
     */
    public int getQueueCount(String queueName) {
        final String key = "ApproximateNumberOfMessages";
        final GetQueueAttributesResponse result = sqs.getQueueAttributes(req -> req.queueUrl(queueName)
                                                                                   .attributeNamesWithStrings(List.of(
                                                                                           key)));
        final String value = result.attributesAsStrings().getOrDefault(key, "0");
        return Integer.parseInt(value);
    }

    /**
     * Determines whether a queue with the specified name exists.
     *
     * @param queueName the name of the queue
     * @return true if the queue exists, false otherwise
     */
    public boolean exists(String queueName) {
        try {
            getQueueUrl(queueName);
            return true;
        } catch (QueueDoesNotExistException e) {
            return false;
        }
    }

    /**
     * Creates a new queue with the specified queue name.
     *
     * @param queueName the name of the queue to be created
     * @return the URL of the newly created queue
     */
    public String create(String queueName) {
        return LocalstackSqsConfig.createQueue(sqs, queueName).queueUrl();
    }

    /**
     * Retrieves the URL of a queue with the specified name.
     *
     * @param queueName the name of the queue to retrieve the URL for
     * @return the URL of the queue
     */
    public String getQueueUrl(String queueName) {
        return sqs.getQueueUrl(req -> req.queueName(queueName)).queueUrl();
    }

    /**
     * Retrieves the ARN of a queue with the specified name.
     *
     * @param queueName the name of the queue to retrieve the URL for
     * @return the ARN of the queue for use with subscriptions, IAM, etc.
     */
    public String getQueueArn(String queueName) {
        String qUrl = getQueueUrl(queueName);
        return sqs.getQueueAttributes(req -> req.queueUrl(qUrl)
                                                .attributeNames(QUEUE_ARN))
                  .attributes()
                  .get(QUEUE_ARN);
    }

    /**
     * Sends a message with the specified name and body.
     *
     * @param name the name of the message to be sent
     * @param body the body of the message to be sent
     */
    public void send(String name, String body) {
        convertAndSend(name, body);
    }

    /**
     * Converts and sends the specified message to a specified destination queue.
     *
     * @param qName   the name of the destination queue to which the message is to be sent
     * @param message the message to be sent to the destination queue
     */
    public void convertAndSend(String qName, Object message) {
        convertAndSend(qName, message, null);
    }

    /**
     * Converts and sends a message to a specified queue.
     *
     * @param qName           the name of the queue to send the message to
     * @param message         the message to send
     * @param attributeValues optional sqs attribute values for the message
     */
    public void convertAndSend(String qName, Object message, Map<String, Object> attributeValues) {
        sqsSender.send(getQueueUrl(qName), message, attributeValues);
    }

    /**
     * Waits for a message to be available in the specified queue.  Maximum seconds to wait defaults to MEDIUM_POLL.
     *
     * @param queueName the name of the queue to wait for a message
     * @return the message that was received from the queue
     * @see #MEDIUM_POLL
     */
    public Message waitForMessage(String queueName) {
        return waitForMessages(queueName, MEDIUM_POLL).get(0);
    }

    /**
     * Waits for a message to be available in the specified queue.
     *
     * @param queueName       the name of the queue to wait for a message
     * @param waitTimeSeconds maximum seconds to wait.
     * @return the message that was received from the queue
     * @see #SHORT_POLL
     * @see #MEDIUM_POLL
     * @see #MAX_POLL
     */
    public Message waitForMessage(String queueName, int waitTimeSeconds) {
        return waitForMessages(queueName, waitTimeSeconds).get(0);
    }

    /**
     * Waits for messages to be available in the specified queue.
     *
     * @param queueName       the name of the queue to wait for a message
     * @param waitTimeSeconds maximum seconds to wait.
     * @return the list of message that was received from the queue (size not guaranteed as polling is async).
     * @see #SHORT_POLL
     * @see #MEDIUM_POLL
     * @see #MAX_POLL
     */
    @SneakyThrows
    public List<Message> waitForMessages(String queueName, int waitTimeSeconds) {
        if (waitTimeSeconds > MAX_POLL) {
            throw new IllegalArgumentException(format("Max wait time for SQS poll is %d seconds", MAX_POLL));
        }
        final String queueUrl = getQueueUrl(queueName);
        final ReceiveMessageResponse result = sqs.receiveMessage(req -> req.queueUrl(queueUrl)
                                                                           .waitTimeSeconds(waitTimeSeconds));
        final List<Message> messages = result.messages();
        if (messages.isEmpty()) {
            throw new TimeoutException("Gave up waiting for message on " + queueName);
        } else {
            // acknowledge messages
            messages.forEach(m -> sqs.deleteMessage(req -> req.queueUrl(queueUrl).receiptHandle(m.receiptHandle())));
            log.info("Received {} messages", messages.size());
            return messages;
        }
    }

    /**
     * Waits for messages to become available in the specified queue.  Max wait defaults to MEDIUM_POLL.
     *
     * @param queueName the name of the queue to wait for messages in
     * @param clazz     the class representing the type of messages to wait for
     * @param <T>       Type of message body
     * @return a list of messages of type T that have been received from the queue
     * @see #MEDIUM_POLL
     */
    public <T> List<T> waitForMessages(String queueName, Class<T> clazz) {
        return waitForMessages(queueName, MEDIUM_POLL, clazz);
    }

    /**
     * Waits for messages to become available in the specified queue.  Max wait defaults to MEDIUM_POLL.
     *
     * @param queueName     the name of the queue to wait for messages in
     * @param typeReference the type reference representing the type of messages to wait for
     * @param <T>           Type of message body
     * @return a list of messages of type T that have been received from the queue
     * @see #MEDIUM_POLL
     */
    public <T> List<T> waitForMessages(String queueName, TypeReference<T> typeReference) {
        return waitForMessages(queueName, MEDIUM_POLL, typeReference);
    }

    /**
     * Waits for messages to become available in the specified queue.
     *
     * @param queueName       the name of the queue to wait for messages in
     * @param waitTimeSeconds maximum seconds to wait.
     * @param clazz           the class representing the type of messages to wait for
     * @param <T>             Type of message body
     * @return a list of messages of type T that have been received from the queue
     * @see #SHORT_POLL
     * @see #MEDIUM_POLL
     * @see #MAX_POLL
     */
    public <T> List<T> waitForMessages(String queueName, int waitTimeSeconds, Class<T> clazz) {
        return waitForMessages(queueName, waitTimeSeconds).stream()
                                                          .map(m -> toObject(m.body(), clazz))
                                                          .collect(toList());
    }

    /**
     * Waits for messages to become available in the specified queue.
     *
     * @param queueName       the name of the queue to wait for messages in
     * @param waitTimeSeconds maximum seconds to wait.
     * @param typeReference   the type reference representing the type of messages to wait for
     * @param <T>             Type of message body
     * @return a list of messages of type T that have been received from the queue
     * @see #SHORT_POLL
     * @see #MEDIUM_POLL
     * @see #MAX_POLL
     */
    public <T> List<T> waitForMessages(String queueName, int waitTimeSeconds, TypeReference<T> typeReference) {
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
     */
    public <T> List<T> waitForNotificationMessages(String queueName, int waitTimeSeconds, Class<T> clazz) {
        return waitForMessages(queueName, waitTimeSeconds).stream()
                                                          .map(m -> toObject(m.body(), Map.class))
                                                          .map(sns -> toObject(snsMessage(sns), clazz))
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
     */
    public <T> List<T> waitForNotificationMessages(String queueName, int waitTimeSeconds, TypeReference<T> type) {
        return waitForMessages(queueName, waitTimeSeconds).stream()
                                                          .map(m -> toObject(m.body(), Map.class))
                                                          .map(sns -> toObject(snsMessage(sns), type))
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
     */
    public <T> List<T> waitUntilNotificationMessageCountGreaterThan(String queueName,
                                                                    int waitUntilTimeSeconds,
                                                                    Class<T> clazz,
                                                                    int messageCountMin) {
        return waitUntilMessageCountGreaterThan(queueName, waitUntilTimeSeconds, messageCountMin)
                .stream()
                .map(m -> toObject(m.body(), Map.class))
                .map(sns -> snsToObject(sns, clazz))
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
     */
    public <T> List<T> waitUntilNotificationMessageCountGreaterThan(String queueName,
                                                                    int waitUntilTimeSeconds,
                                                                    TypeReference<T> type,
                                                                    int messageCountMin) {
        return waitUntilMessageCountGreaterThan(queueName, waitUntilTimeSeconds, messageCountMin)
                .stream()
                .map(m -> toObject(m.body(), Map.class))
                .map(sns -> snsToObject(sns, type))
                .collect(toList());
    }

    /**
     * Waits until the message count in a specified queue exceeds a specified minimum,
     * and then returns the list of messages collected during the wait period.
     *
     * @param queueName            the name of the queue
     * @param waitUntilTimeSeconds the maximum number of seconds to wait
     * @param messageCountMin      the minimum number of messages to collect
     * @param pollSeconds          the number of seconds to wait between polling the queue
     * @return the list of collected messages
     */
    @SneakyThrows
    public List<Message> waitUntilMessageCountGreaterThan(String queueName,
                                                          int waitUntilTimeSeconds,
                                                          int messageCountMin,
                                                          int pollSeconds) {
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

    /**
     * Waits until the message count in a specified queue exceeds a specified minimum,
     * and then returns the list of messages collected during the wait period.  The polling interval is
     * defaulted to SHORT_POLL.
     *
     * @param queueName            the name of the queue
     * @param waitUntilTimeSeconds the maximum number of seconds to wait
     * @param messageCountMin      the minimum number of messages to collect
     * @return the list of collected messages
     * @see #SHORT_POLL
     */
    public List<Message> waitUntilMessageCountGreaterThan(String queueName,
                                                          int waitUntilTimeSeconds,
                                                          int messageCountMin) {
        return waitUntilMessageCountGreaterThan(queueName, waitUntilTimeSeconds, messageCountMin, SHORT_POLL);
    }


    /**
     * Waits until the message count in a specified queue exceeds a specified minimum,
     * and then returns the list of messages collected during the wait period.  The polling interval is
     * defaulted to SHORT_POLL.
     *
     * @param queueName            the name of the queue
     * @param waitUntilTimeSeconds the maximum number of seconds to wait
     * @param messageCountMin      the minimum number of messages to collect
     * @param clazz                Class of the Type of messages to receive.
     * @param <T>                  Type of messages to receive.
     * @return the list of collected messages
     * @see #SHORT_POLL
     */
    public <T> List<T> waitUntilMessageCountGreaterThan(String queueName,
                                                        int waitUntilTimeSeconds,
                                                        int messageCountMin,
                                                        Class<T> clazz) {
        return waitUntilMessageCountGreaterThan(queueName, waitUntilTimeSeconds, messageCountMin, SHORT_POLL, clazz);
    }

    /**
     * Waits until the message count in a specified queue exceeds a specified minimum,
     * and then returns the list of messages collected during the wait period.  The polling interval is
     * defaulted to SHORT_POLL.
     *
     * @param queueName            the name of the queue
     * @param waitUntilTimeSeconds the maximum number of seconds to wait
     * @param messageCountMin      the minimum number of messages to collect
     * @param type                 Type Reference of the Type of messages to receive.
     * @param <T>                  Type of messages to receive.
     * @return the list of collected messages
     * @see #SHORT_POLL
     */
    public <T> List<T> waitUntilMessageCountGreaterThan(String queueName,
                                                        int waitUntilTimeSeconds,
                                                        int messageCountMin,
                                                        TypeReference<T> type) {
        return waitUntilMessageCountGreaterThan(queueName, waitUntilTimeSeconds, messageCountMin, SHORT_POLL, type);
    }

    /**
     * Waits until the message count in a specified queue exceeds a specified minimum,
     * and then returns the list of messages collected during the wait period.
     *
     * @param queueName            the name of the queue
     * @param waitUntilTimeSeconds the maximum number of seconds to wait
     * @param messageCountMin      the minimum number of messages to collect
     * @param clazz                Class of the Type of messages to receive.
     * @param pollSeconds          interval between sqs polling.
     * @param <T>                  Type of messages to receive.
     * @return the list of collected messages
     */
    public <T> List<T> waitUntilMessageCountGreaterThan(String queueName,
                                                        int waitUntilTimeSeconds,
                                                        int messageCountMin,
                                                        int pollSeconds,
                                                        Class<T> clazz) {
        List<Message> found = waitUntilMessageCountGreaterThan(queueName,
                                                               waitUntilTimeSeconds,
                                                               messageCountMin,
                                                               pollSeconds);
        return found.stream()
                    .map(m -> toObject(m.body(), clazz))
                    .collect(toList());
    }

    /**
     * Waits until the message count in a specified queue exceeds a specified minimum,
     * and then returns the list of messages collected during the wait period.
     *
     * @param queueName            the name of the queue
     * @param waitUntilTimeSeconds the maximum number of seconds to wait
     * @param messageCountMin      the minimum number of messages to collect
     * @param type                 Type Reference of the Type of messages to receive.
     * @param pollSeconds          interval between sqs polling.
     * @param <T>                  Type of messages to receive.
     * @return the list of collected messages
     */
    public <T> List<T> waitUntilMessageCountGreaterThan(String queueName,
                                                        int waitUntilTimeSeconds,
                                                        int messageCountMin,
                                                        int pollSeconds,
                                                        TypeReference<T> type) {
        List<Message> found = waitUntilMessageCountGreaterThan(queueName,
                                                               waitUntilTimeSeconds,
                                                               messageCountMin,
                                                               pollSeconds);
        return found.stream()
                    .map(m -> toObject(m.body(), type))
                    .collect(toList());
    }

    /**
     * Purges all messages from the specified queue using individual deletes.
     *
     * @param queueName the name of the queue to purge
     */
    public void purge(String queueName) {
        final String url = getQueueUrl(queueName);
        ReceiveMessageResponse receiveMessageResult = sqs.receiveMessage(r -> r.queueUrl(url));
        int count = 0;
        while (!receiveMessageResult.messages().isEmpty()) {
            final List<Message> messages = receiveMessageResult.messages();
            for (Message message : messages) {
                sqs.deleteMessage(r -> r.queueUrl(url).receiptHandle(message.receiptHandle()));
                count++;
            }
            receiveMessageResult = sqs.receiveMessage(r -> r.queueUrl(url));
        }
        log.info("Purged {} messages", count);
    }

    @SneakyThrows
    private <T> T toObject(String json, TypeReference<T> type) {
        return objectMapper.readValue(json, type);
    }

    @SneakyThrows
    private <T> T toObject(String json, Class<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }

    @SuppressWarnings("rawtypes")
    private <T> T snsToObject(Map sns, TypeReference<T> type) {
        return toObject(snsMessage(sns), type);
    }

    @SuppressWarnings("rawtypes")
    private <T> T snsToObject(Map sns, Class<T> clazz) {
        return toObject(snsMessage(sns), clazz);
    }

    @SuppressWarnings("rawtypes")
    private static String snsMessage(Map sns) {
        return (String) sns.get("Message");
    }
}
