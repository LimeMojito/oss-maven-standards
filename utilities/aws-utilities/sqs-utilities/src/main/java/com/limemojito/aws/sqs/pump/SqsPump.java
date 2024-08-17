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

package com.limemojito.aws.sqs.pump;

import com.limemojito.aws.sqs.SqsSender;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.BatchResultErrorEntry;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * SQS Pump is designed to be used in a multithreaded context where with exclusive flush semantics.  Two threads writing
 * to the same destination will get one batch sent.  Attributes are set to be compatible with spring cloud messaging.
 *
 * <ul>
 *     <li>id - unique id for each message</li>
 *     <li>timestamp - epoch milliseconds</li>
 *     <li>contentType - mime type</li>
 *     <li>Content-Type - Mime like attribute</li>
 *     <li>Content-Length - Mime like attribute</li>
 * </ul>
 */
@Component
@Slf4j
public class SqsPump implements AutoCloseable {
    private final SqsSender sqsSender;
    private final int pumpMaxBatchSize;
    private final Map<String, ConcurrentLinkedDeque<SqsPumpMessage>> localPump;

    /**
     * Constructs a new instance of the SqsPump class.
     *
     * @param sqsSender        Use SQS Sender to send messages with attributes set.
     * @param pumpMaxBatchSize The maximum number of messages to send in a single batch from SQS.
     */
    public SqsPump(SqsSender sqsSender,
                   @Value("${com.limemojito.sqs.batchSize:10}") int pumpMaxBatchSize) {
        this.sqsSender = sqsSender;
        this.pumpMaxBatchSize = pumpMaxBatchSize;
        this.localPump = new ConcurrentHashMap<>();
        log.info("Initialized SQS Pump with max batch size {}", pumpMaxBatchSize);
    }

    /**
     * Sends a message by adding it to the batch to be flushed by more message sends, or the flush method.
     *
     * @param destination     Destination to send message batch to.  May be qName or qUrl, url is more efficient.
     * @param jsonableMessage message to send
     * @see #flush(String)
     * @see #flushAll()
     */
    public void send(String destination, Object jsonableMessage) {
        send(destination, jsonableMessage, null);
    }

    /**
     * Sends a message by adding it to the batch to be flushed by more message sends, or the flush method.  For FIFO
     * queues message-group-id and message-deduplication-id must be supplied.
     * <p>
     * Note headers for ID, contentType and Timestamp (epoch millis) are set to ensure spring messaging compatibility.
     *
     * @param destination     Destination to send message batch to.  May be qName or qUrl, url is more efficient.
     * @param jsonableMessage message to send
     * @param attributes      attributes to apply to message as SQS Attributes.  May also include FIFO info as headers.
     * @see #flush(String)
     * @see #flushAll()
     * @see SqsSender#ATTRIBUTE_MESSAGE_GROUP_ID
     * @see SqsSender#ATTRIBUTE_MESSAGE_DEDUPLICATION_ID
     */
    public void send(String destination, Object jsonableMessage, Map<String, Object> attributes) {
        if (pumpFor(destination).size() >= pumpMaxBatchSize) {
            flush(destination);
        }
        pumpFor(destination).add(new SqsPumpMessage(jsonableMessage, attributes));
    }

    /**
     * Flushes messages to the specified destination.
     *
     * @param destination the destination to flush messages to.
     */
    public synchronized void flush(String destination) {
        final Deque<SqsPumpMessage> volatileMessages = pumpFor(destination);
        while (!volatileMessages.isEmpty()) {
            final List<SqsPumpMessage> messages = removeMessagesToSend(volatileMessages);
            log.trace("Flushing {} messages to {}", messages.size(), destination);
            Map<Object, Map<String, Object>> toRaw = new LinkedHashMap<>();
            messages.forEach(message -> toRaw.put(message.getMessage(), message.getAttributes()));
            final SendMessageBatchResponse sendMessageBatchResult = sqsSender.sendBatch(destination, toRaw);
            final List<BatchResultErrorEntry> failed = sendMessageBatchResult.failed();
            if (!failed.isEmpty()) {
                log.error("{} messages failed to {}", failed.size(), destination);
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

    /**
     * Flushes all destinations by iterating over the set of destinations maintained in the localPump object and calling the flush method for each destination.
     * This method should be called before destroying the object.
     * <p>
     * {@code @PreDestroy } annotation is used to indicate that this method should be called before the object is destroyed.
     * It is important to call this method to ensure that any pending data in the destinations is flushed before destroying the object.
     *
     * @see #flush(String)
     */
    @PreDestroy
    public void flushAll() {
        final Set<String> destinations = localPump.keySet();
        for (String destination : destinations) {
            flush(destination);
        }
    }

    /**
     * Closes the resource and flushes any pending data.
     * <p>
     * This method overrides the close() method from the parent class. It should be called to release any resources held by the object and to ensure that any pending data is written
     * before closing the resource.
     */
    @Override
    public void close() {
        flushAll();
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

}
