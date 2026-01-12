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


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import tools.jackson.databind.json.JsonMapper;

import java.util.*;
import java.util.function.Supplier;

import static java.util.Collections.emptyMap;

/**
 * Sends Messages to SQS using standardized message attributes compatible with Spring Messaging and general
 * MIME type conventions.  Supports FIFO and batch sending.
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
@RequiredArgsConstructor
@Slf4j
public class SqsSender {

    /**
     * This constant represents the header name for the deduplication ID of a message.
     * The value of this header is expected to be a unique identifier for the message,
     * used for identifying and avoiding processing duplicate messages.
     * <p>
     * The constant is a string with the value "message-deduplication-id".
     */
    public static final String ATTRIBUTE_MESSAGE_DEDUPLICATION_ID = "message-deduplication-id";

    /**
     * Represents the header for application generated id.
     * <p>
     * The constant is a string with the value "id".
     */
    public static final String ATTRIBUTE_ID = "id";

    /**
     * Represents the header for application generated timestamp in epoch milliseconds.
     * <p>
     * The constant is a string with the value "timestamp".
     */
    public static final String ATTRIBUTE_TIMESTAMP = "timestamp";

    /**
     * Represents the header for message body content length in a MIME like style.
     * <p>
     * The constant is a string with the value "Content-Length".
     */
    public static final String ATTRIBUTE_CONTENT_LENGTH = "Content-Length";

    /**
     * Represents the header for message body content type in a MIME like style.
     * <p>
     * The constant is a string with the value "Content-Type".
     */
    public static final String ATTRIBUTE_CONTENT_TYPE = "Content-Type";

    /**
     * Represents the header for message body content type in a MIME like style using a name compatible with Spring
     * Messaging.
     * <p>
     * The constant is a string with the value "contentType".
     */
    public static final String ATTRIBUTE_SPRING_CONTENT_TYPE = "contentType";

    /**
     * The constant variable ATTRIBUTE_MESSAGE_GROUP_ID represents the name of the header that
     * stores the unique identifier of a message group.
     * <p>
     * The value of this variable is set to "message-group-id".
     */
    public static final String ATTRIBUTE_MESSAGE_GROUP_ID = "message-group-id";

    private static final String JSON_CONTENT = "application/json";

    private final SqsClient sqs;
    private final JsonMapper objectMapper;

    /**
     * Sends a message to a specified queue URL.
     * <p>
     * The message will be enriched with standard attributes and the body as json.
     *
     * @param queueUrl the URL of the queue to which the message is sent
     * @param message  the message to be sent
     */
    public void send(String queueUrl, Object message) {
        send(queueUrl, message, emptyMap());
    }

    /**
     * Sends a message to the specified queue.
     * <p>
     * The message will be enriched with standard attributes and the body as json.To send fifo information, you must include
     * ATTRIBUTE_MESSAGE_DEDUPLICATION_ID and ATTRIBUTE_MESSAGE_GROUP_ID in the attributeValues.
     *
     * @param queueUrl        the URL of the queue to send the message to
     * @param message         the message to send
     * @param attributeValues additional attribute values for the message
     * @see #ATTRIBUTE_MESSAGE_DEDUPLICATION_ID
     * @see #ATTRIBUTE_MESSAGE_GROUP_ID
     */
    public void send(String queueUrl, Object message, Map<String, Object> attributeValues) {
        assertFifo(queueUrl, attributeValues);
        SendMessageRequest.Builder r = SendMessageRequest.builder();
        r.queueUrl(queueUrl);
        final String body = toJson(message);
        r.messageBody(body);
        fifoOptions(r, queueUrl, attributeValues);
        if (attributeValues != null && !attributeValues.isEmpty()) {
            r.messageAttributes(sqsAttrFrom(
                    body.length(),
                    attributeValues));
        }
        sqs.sendMessage(r.build());
        log.info("Sent message: {} {} to {}", message, attributeValues, queueUrl);
    }

    /**
     * Sends a set of message to the specified queue using batch request.
     * <p>
     * The message will be enriched with standard attributes and the body as json.  This method does not support FIFO
     * queues.
     *
     * @param queueUrl the URL of the queue to send the message to
     * @param messages Map of message object to message attributes.
     * @return Message batch sent.
     */
    public SendMessageBatchResponse sendBatch(String queueUrl,
                                              Collection<Object> messages) {
        Map<Object, Map<String, Object>> messageAttributeMap = new LinkedHashMap<>();
        for (Object message : messages) {
            messageAttributeMap.put(message, emptyMap());
        }
        return sendBatch(queueUrl, messageAttributeMap);
    }

    /**
     * Sends a set message to the specified queue using batch request.
     * <p>
     * The message will be enriched with standard attributes and the body as json.To send fifo information, you must include
     * ATTRIBUTE_MESSAGE_DEDUPLICATION_ID and ATTRIBUTE_MESSAGE_GROUP_ID in the attributeValues.
     *
     * @param queueUrl            the URL of the queue to send the message to
     * @param messageAttributeMap Map of message object to message attributes.
     * @return Message batch sent.
     * @see #ATTRIBUTE_MESSAGE_DEDUPLICATION_ID
     * @see #ATTRIBUTE_MESSAGE_GROUP_ID
     */
    public SendMessageBatchResponse sendBatch(String queueUrl,
                                              Map<Object, Map<String, Object>> messageAttributeMap) {
        messageAttributeMap.values().forEach(att -> assertFifo(queueUrl, att));
        SendMessageBatchRequest.Builder r = SendMessageBatchRequest.builder();
        r.queueUrl(queueUrl);
        final List<SendMessageBatchRequestEntry> entries = new ArrayList<>(messageAttributeMap.size());
        int i = 0;
        for (Map.Entry<Object, Map<String, Object>> messageEntry : messageAttributeMap.entrySet()) {
            final SendMessageBatchRequestEntry entry = createBatchEntry(queueUrl,
                                                                        i,
                                                                        messageEntry.getKey(),
                                                                        messageEntry.getValue());
            entries.add(entry);
        }
        r.entries(entries);
        log.info("Sending batch request for {} size {}", queueUrl, messageAttributeMap.size());
        return sqs.sendMessageBatch(r.build());
    }

    private void assertFifo(String queueUrl, Map<String, Object> attributeValues) {
        if (isFifo(queueUrl)) {
            if (attributeValues == null
                    || attributeValues.isEmpty()
                    || !attributeValues.containsKey(ATTRIBUTE_MESSAGE_DEDUPLICATION_ID)
                    || !attributeValues.containsKey(ATTRIBUTE_MESSAGE_GROUP_ID)) {
                throw new IllegalArgumentException(
                        "If you want to send a fifo message, you must include %s and %s in the attributeValues".formatted(
                                ATTRIBUTE_MESSAGE_GROUP_ID,
                                ATTRIBUTE_MESSAGE_DEDUPLICATION_ID));
            }
        }
    }

    private Map<String, MessageAttributeValue> sqsAttrFrom(int contentLength,
                                                           Map<String, Object> attributes) {
        Map<String, MessageAttributeValue> attributeMap = new LinkedHashMap<>();
        if (attributes != null) {
            attributes.forEach((key, value) -> {
                if (notFifoHeader(key)) {
                    attributeMap.put(key, toValue(value));
                }
            });
        }
        checkValue(attributeMap, ATTRIBUTE_ID, UUID::randomUUID);
        checkValue(attributeMap, ATTRIBUTE_TIMESTAMP, System::currentTimeMillis);
        checkValue(attributeMap, ATTRIBUTE_SPRING_CONTENT_TYPE, () -> JSON_CONTENT);
        checkValue(attributeMap, ATTRIBUTE_CONTENT_TYPE, () -> JSON_CONTENT);
        checkValue(attributeMap, ATTRIBUTE_CONTENT_LENGTH, () -> contentLength);
        log.trace("Generated attribute map {}", attributeMap);
        return attributeMap;
    }

    private static boolean notFifoHeader(String key) {
        return !(ATTRIBUTE_MESSAGE_DEDUPLICATION_ID.equals(key) || (ATTRIBUTE_MESSAGE_GROUP_ID.equals(key)));
    }

    private static MessageAttributeValue toValue(Object value) {
        return (value instanceof Number) ? attr("Number", value)
                                         : attr("String", value);
    }

    private void checkValue(Map<String, MessageAttributeValue> map,
                            String id,
                            Supplier<Object> supplier) {
        if (map.get(id) == null) {
            map.put(id, toValue(supplier.get()));
        }
    }

    private static MessageAttributeValue attr(String dataType, Object value) {
        return MessageAttributeValue.builder()
                                    .dataType(dataType)
                                    .stringValue(value.toString())
                                    .build();
    }

    private SendMessageBatchRequestEntry createBatchEntry(String queueUrl,
                                                          int index,
                                                          Object message,
                                                          Map<String, Object> attributes) {
        final String body = toJson(message);
        final int length = body.length();
        final SendMessageBatchRequestEntry.Builder entry;
        entry = SendMessageBatchRequestEntry.builder()
                                            .id(Integer.toString(index))
                                            .messageBody(body);
        if (isFifo(queueUrl) && attributes != null) {
            attributes.keySet().forEach(key -> checkFifoOption(entry, attributes, key));
        }
        entry.messageAttributes(sqsAttrFrom(length, attributes));
        return entry.build();

    }

    private static boolean isFifo(String queueUrl) {
        return queueUrl.endsWith("fifo");
    }

    @SneakyThrows
    private String toJson(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    private void fifoOptions(SendMessageRequest.Builder r, String queueUrl, Map<String, Object> attributeValues) {
        if (isFifo(queueUrl) && attributeValues != null) {
            for (String key : attributeValues.keySet()) {
                checkFifoOption(r, attributeValues, key);
            }
        }
    }

    private static void checkFifoOption(SendMessageBatchRequestEntry.Builder entry,
                                        Map<String, Object> attributes,
                                        String key) {
        final Object value = attributes.get(key);
        if (key.equals(ATTRIBUTE_MESSAGE_DEDUPLICATION_ID)) {
            entry.messageDeduplicationId(value.toString());
        } else if (key.equals(ATTRIBUTE_MESSAGE_GROUP_ID)) {
            entry.messageGroupId(value.toString());
        }
    }

    private static void checkFifoOption(SendMessageRequest.Builder request,
                                        Map<String, Object> attributeValues,
                                        String key) {
        final Object value = attributeValues.get(key);
        if (ATTRIBUTE_MESSAGE_DEDUPLICATION_ID.equals(key)) {
            request.messageDeduplicationId(value.toString());
        } else if (ATTRIBUTE_MESSAGE_GROUP_ID.equals(key)) {
            request.messageGroupId(value.toString());
        }
    }
}
