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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limemojito.aws.sqs.SqsSender;
import com.limemojito.json.ObjectMapperPrototype;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageHeaders;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SqsPumpTest {

    private final ObjectMapper objectMapper = ObjectMapperPrototype.buildBootLikeMapper();
    private final int pumpMaxBatchSize = 10;
    private final String queueUrl = "sqs://queue/url";
    @Mock
    private SqsClient sqs;
    @Captor
    private ArgumentCaptor<SendMessageBatchRequest> requestCaptor;
    private SqsPump sqsPump;

    @BeforeEach
    void setUp() {
        sqsPump = new SqsPump(new SqsSender(sqs, objectMapper), pumpMaxBatchSize);
    }

    @Test
    public void shouldPumpMessagesOk() {
        performBatchTest(pumpMaxBatchSize);
    }

    @Test
    public void shouldPumpOneHundredMessages() {
        performBatchTest(100);
    }

    @Test
    public void shouldPumpThirtyThreeMessages() {
        performBatchTest(33);
    }

    @Test
    public void shouldSendAFifoMessage() {
        whenBatchSendOk();

        final Map<String, Object> fifoHeaders = Map.of("message-deduplication-id", "identity",
                                                       "message-group-id", "somegroup");
        sqsPump.send(queueUrl + ".fifo", "test message", fifoHeaders);
        sqsPump.flushAll();

        verify(sqs).sendMessageBatch(requestCaptor.capture());
        final SendMessageBatchRequest value = requestCaptor.getValue();
        assertThat(value.entries()).hasSize(1);
        SendMessageBatchRequestEntry entry = value.entries().get(0);
        assertThat(entry.id()).isEqualTo("0");
        assertThat(entry.messageBody()).isEqualTo("\"test message\"");
        assertThat(entry.messageDeduplicationId()).isEqualTo("identity");
        assertThat(entry.messageGroupId()).isEqualTo("somegroup");
        assertCommonAttributes(entry);
    }

    @Test
    public void shouldNotPumpZeroMessages() {
        sqsPump.flush(queueUrl);

        verifyNoInteractions(sqs);
    }

    private void assertCommonAttributes(SendMessageBatchRequestEntry entry) {
        assertThat(entry.messageAttributes()).containsKey(MessageHeaders.ID);
        assertThat(entry.messageAttributes()).containsEntry(MessageHeaders.CONTENT_TYPE,
                                                            MessageAttributeValue.builder()
                                                                                 .dataType("String")
                                                                                 .stringValue("application/json")
                                                                                 .build());
        assertThat(entry.messageAttributes()).containsKey(MessageHeaders.TIMESTAMP);
        assertThat(entry.messageAttributes()
                        .get(MessageHeaders.TIMESTAMP)
                        .dataType()).isEqualTo("Number");
    }

    private void performBatchTest(int sendSize) {
        whenBatchSendOk();

        for (int i = 0; i < sendSize; i++) {
            sqsPump.send(queueUrl, new TestMessage(i));
        }
        sqsPump.flush(queueUrl);

        verify(sqs, times(expectedBatchSends(sendSize))).sendMessageBatch(requestCaptor.capture());
        final List<SendMessageBatchRequest> allValues = requestCaptor.getAllValues();
        for (int i = 0; i < allValues.size(); i++) {
            SendMessageBatchRequest batchRequest = allValues.get(i);
            int start = pumpMaxBatchSize * i;
            assertThat(batchRequest.queueUrl()).isEqualTo(queueUrl);
            final List<String> bodies = batchRequest.entries()
                                                    .stream()
                                                    .map(SendMessageBatchRequestEntry::messageBody)
                                                    .collect(Collectors.toList());
            assertThat(bodies).containsExactly(generateBodies(start, sendSize));
        }
    }

    private int expectedBatchSends(int sendSize) {
        return sendSize / pumpMaxBatchSize + (sendSize % pumpMaxBatchSize > 0 ? 1 : 0);
    }

    private void whenBatchSendOk() {
        doReturn(SendMessageBatchResponse.builder().build())
                .when(sqs)
                .sendMessageBatch(any(SendMessageBatchRequest.class));
    }

    private String[] generateBodies(int start, int sendSize) {
        final int messageSize = Math.min(sendSize - start, pumpMaxBatchSize);
        final String[] messages = new String[messageSize];
        for (int i = 0; i < messageSize; i++) {
            messages[i] = format("{\"index\":%d}", start + i);
        }
        return messages;
    }
}
