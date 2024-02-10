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

package com.limemojito.aws.sqs.pump;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * SQS Pump is designed to be used in a multithreaded context where with exclusive flush semantics.  Two threads writing
 * to the same destination will get one batch sent.  Headers are set to be compatible with spring cloud messaging.
 *
 * @see MessageHeaders#ID
 * @see MessageHeaders#CONTENT_TYPE
 * @see MessageHeaders#TIMESTAMP
 */
@Component
@Slf4j
public class SqsPump implements AutoCloseable {
    public static final String HEADER_MESSAGE_DEDUPLICATION_ID = "message-deduplication-id";
    public static final String HEADER_MESSAGE_GROUP_ID = "message-group-id";
    private final SqsAsyncClient sqs;
    private final int pumpMaxBatchSize;
    private final ObjectMapper objectMapper;
    private final Map<String, ConcurrentLinkedDeque<SqsPumpMessage>> localPump;

    public SqsPump(SqsAsyncClient sqs,
                   ObjectMapper objectMapper,
                   @Value("${com.limemojito.sqs.batchSize:10}") int pumpMaxBatchSize) {
        this.sqs = sqs;
        this.pumpMaxBatchSize = pumpMaxBatchSize;
        this.objectMapper = objectMapper;
        this.localPump = new ConcurrentHashMap<>();
        log.info("Initialized SQS Pump with max batch size {}", pumpMaxBatchSize);
    }

    public void send(String destination, Object jsonableMessage) {
        send(destination, jsonableMessage, null);
    }

    /**
     * Sends a message by adding it to the batch to be flushed by more message sends, or the flush method.
     * <p>
     * Note headers for ID, contentType and Timestamp (epoch millis) are set to ensure spring messaging compatibility.
     *
     * @param destination     Destination to send message batch to.  May be qName or qUrl, url is more efficient.
     * @param jsonableMessage message to send
     * @param headers         headers to apply to message as SQS Attributes.  May also include FIFO info as headers.
     * @see #flush(String)
     * @see #flushAll()
     * @see #HEADER_MESSAGE_DEDUPLICATION_ID
     * @see #HEADER_MESSAGE_GROUP_ID
     */
    public void send(String destination, Object jsonableMessage, Map<String, Object> headers) {
        if (pumpFor(destination).size() >= pumpMaxBatchSize) {
            flush(destination);
        }
        pumpFor(destination).add(new SqsPumpMessage(jsonableMessage, headers));
    }

    public synchronized void flush(String destination) {
        final Deque<SqsPumpMessage> volatileMessages = pumpFor(destination);
        while (!volatileMessages.isEmpty()) {
            final List<SqsPumpMessage> messages = removeMessagesToSend(volatileMessages);
            log.trace("Flushing {} messages to {}", messages.size(), destination);
            final SendMessageBatchRequest batchRequest = createBatchRequest(destination, messages);
            final SendMessageBatchResponse sendMessageBatchResult = sqs.sendMessageBatch(batchRequest).join();
            final List<BatchResultErrorEntry> failed = sendMessageBatchResult.failed();
            if (!failed.isEmpty()) {
                log.error("{} messages failed to {}", failed.size(), batchRequest.queueUrl());
                for (BatchResultErrorEntry batchResultErrorEntry : failed) {
                    log.error("Failure onSender={} {}:{}",
                              batchResultErrorEntry.senderFault(),
                              batchResultErrorEntry.code(),
                              batchResultErrorEntry.message());
                }
                throw new IllegalStateException("Could not send all messages to SQS " + destination);
            } else {
                log.debug("Sent {} messages to {} ok", messages.size(), destination);
            }
        }
    }

    @PreDestroy
    public void flushAll() {
        final Set<String> destinations = localPump.keySet();
        for (String destination : destinations) {
            flush(destination);
        }
    }

    @Override
    public void close() {
        flushAll();
    }

    @SneakyThrows
    private String toJson(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    private SendMessageBatchRequestEntry createEntry(int i, SqsPumpMessage sqsPumpMessage) {
        final Map<String, Object> attributeValues = buildMessageAttributes(sqsPumpMessage.getHeaders());
        final SendMessageBatchRequestEntry.Builder entry;
        entry = SendMessageBatchRequestEntry.builder()
                                            .id(Integer.toString(i))
                                            .messageBody(toJson(sqsPumpMessage.getMessage()));
        processMessageAttributes(entry, attributeValues);
        return entry.build();
    }

    private Map<String, Object> buildMessageAttributes(Map<String, Object> headers) {
        final Map<String, Object> attributeValues = new LinkedHashMap<>();
        attributeValues.put(MessageHeaders.ID, UUID.randomUUID().toString());
        attributeValues.put(MessageHeaders.CONTENT_TYPE, "application/json");
        attributeValues.put(MessageHeaders.TIMESTAMP, System.currentTimeMillis());
        if (headers != null) {
            attributeValues.putAll(headers);
        }
        return attributeValues;
    }

    private void processMessageAttributes(SendMessageBatchRequestEntry.Builder entry,
                                          Map<String, Object> attributeValues) {
        final Map<String, MessageAttributeValue> attributes = new LinkedHashMap<>();
        for (String key : attributeValues.keySet()) {
            final Object value = attributeValues.get(key);
            if (key.equals(HEADER_MESSAGE_DEDUPLICATION_ID)) {
                entry.messageDeduplicationId(value.toString());
            } else if (key.equals(HEADER_MESSAGE_GROUP_ID)) {
                entry.messageGroupId(value.toString());
            } else {
                final MessageAttributeValue.Builder messageAttributeValue = MessageAttributeValue.builder();
                // for compatibility with spring cloud aws messaging
                messageAttributeValue.dataType(value instanceof Number
                                                       ? "Number." + value.getClass().getName()
                                                       : "String");
                messageAttributeValue.stringValue(value.toString());
                attributes.put(key, messageAttributeValue.build());
            }
        }
        entry.messageAttributes(attributes);
    }

    private Deque<SqsPumpMessage> pumpFor(String destination) {
        return localPump.computeIfAbsent(destination, key -> new ConcurrentLinkedDeque<>());
    }

    private List<SqsPumpMessage> removeMessagesToSend(Deque<SqsPumpMessage> volatileMessages) {
        final List<SqsPumpMessage> messages = new ArrayList<>(pumpMaxBatchSize);
        for (int i = 0; i < pumpMaxBatchSize && !volatileMessages.isEmpty(); i++) {
            messages.add(volatileMessages.removeFirst());
        }
        return messages;
    }

    private SendMessageBatchRequest createBatchRequest(String queueUrl, List<SqsPumpMessage> messages) {
        final SendMessageBatchRequest.Builder batchRequest = SendMessageBatchRequest.builder().queueUrl(queueUrl);
        final List<SendMessageBatchRequestEntry> entries = new ArrayList<>(messages.size());
        for (int i = 0; i < messages.size(); i++) {
            final SqsPumpMessage sqsPumpMessage = messages.get(i);
            final SendMessageBatchRequestEntry entry = createEntry(i, sqsPumpMessage);
            entries.add(entry);
        }
        batchRequest.entries(entries);
        return batchRequest.build();
    }

}
