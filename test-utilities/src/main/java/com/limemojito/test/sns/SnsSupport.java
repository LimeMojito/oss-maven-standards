/*
 * Copyright 2011-2023 Lime Mojito Pty Ltd
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

package com.limemojito.test.sns;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limemojito.aws.sns.LocalstackSnsConfig;
import com.limemojito.test.sqs.SqsSupport;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SnsSupport {
    public static final String HEADER_MESSAGE_DEDUPLICATION_ID = "message-deduplication-id";
    public static final String HEADER_MESSAGE_GROUP_ID = "message-group-id";
    private final SqsSupport sqs;
    private final SnsClient sns;
    private final ObjectMapper mapper;

    public SnsSupport(SqsSupport sqs,
                      SnsClient sns,
                      ObjectMapper mapper) {
        this.sqs = sqs;
        this.sns = sns;
        this.mapper = mapper;
    }

    public String create(String topicName) {
        return getArn(topicName);
    }

    public String getArn(String topicName) {
        return LocalstackSnsConfig.createTopic(sns, topicName).topicArn();
    }

    public void subscribe(String topicName, String queueName) {
        subscribe(topicName, queueName, false);
    }

    public void subscribe(String topicName, String queueName, boolean rawMessageDelivery) {
        final String arn = create(topicName);
        final String queueUrl = sqs.create(queueName);
        sns.subscribe(r -> r.topicArn(arn)
                            .endpoint(queueUrl)
                            .protocol("sqs")
                            .attributes(Map.of("RawMessageDelivery", Boolean.toString(rawMessageDelivery))));
    }

    public void send(String topicName, String body) {
        convertAndSend(topicName, body);
    }

    public void convertAndSend(String topicName, Object message) {
        convertAndSend(topicName, message, null);
    }

    @SneakyThrows
    public void convertAndSend(String topicName, Object message, Map<String, Object> headers) {
        String json = mapper.writeValueAsString(message);
        sns.publish(b -> {
            b.topicArn(getArn(topicName)).message(json);
            if (headers != null) {
                Map<String, MessageAttributeValue> attributes = new LinkedHashMap<>();
                for (String key : headers.keySet()) {
                    String value = headers.get(key).toString();
                    attributes.put(key,
                                   MessageAttributeValue.builder()
                                                        .stringValue(value)
                                                        .dataType("String")
                                                        .build());
                    if (HEADER_MESSAGE_DEDUPLICATION_ID.equals(key)) {
                        b.messageDeduplicationId(value);
                        attributes.remove(key);
                    }
                    if (HEADER_MESSAGE_GROUP_ID.equals(key)) {
                        b.messageGroupId(value);
                        attributes.remove(key);
                    }
                }
                b.messageAttributes(attributes);
            }
        });
    }
}
