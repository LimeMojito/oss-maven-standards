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

package com.limemojito.test.sns;

import com.limemojito.aws.sns.LocalstackSnsConfig;
import com.limemojito.test.sqs.SqsSupport;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The SnsSupport class provides support for interacting with the Amazon Simple Notification Service (SNS).
 * It allows creating and retrieving topics, subscribing queues to topics, and sending messages to topics.
 * <p>
 * This class requires an instance of SqsSupport, SnsClient, and ObjectMapper to be passed to its constructor.
 * These dependencies are used for various operations such as creating topics, subscribing queues to topics, and converting messages to JSON format.
 * <p>
 * The SnsSupport class provides the following functionality:
 * <ol>
 * <li> Creating a topic in the SNS based on a given topic name, returning the ARN of the newly created topic.                                         </li>
 * <li>  Retrieving the ARN of a topic based on a given topic name.                                                                               </li>
 * <li> Subscribing a queue to a topic, with an option to specify if the messages should be delivered as raw (unprocessed) messages.         </li>
 * <li> Sending a message to a specified topic.                                                                                         </li>
 * <li>  Converting and sending a message to a specified topic. The message is converted to JSON format using the provided ObjectMapper.</li>
 * </ol>
 * <p>
 * To use the SnsSupport class, create an instance of it and pass the required dependencies to its constructor.
 * Then, you can use the various methods provided by the class to interact with the SNS service.
 *  <p>This class can also operate on Actual AWS rather than just localstack.</p>
 */
@Service
@RequiredArgsConstructor
public class SnsSupport {
    /**
     * Represents the header key for the deduplication ID in a message sent to the Amazon Simple Notification Service (SNS).
     * The deduplication ID is used to prevent duplicate messages from being sent to the same topic.
     */
    public static final String HEADER_MESSAGE_DEDUPLICATION_ID = "message-deduplication-id";
    /**
     * Represents the header key for the message group ID.
     * The message group ID is used to group messages that belong to the same message group
     * when using Amazon Simple Notification Service (SNS).
     */
    public static final String HEADER_MESSAGE_GROUP_ID = "message-group-id";

    private final SqsSupport sqs;
    private final SnsClient sns;
    private final JsonMapper mapper;

    /**
     * Creates a topic in the Amazon Simple Notification Service (SNS) based on the given topic name.
     * The topic name is used to generate an Amazon Resource Name (ARN) for the newly created topic.
     *
     * @param topicName the name of the topic
     * @return the ARN of the newly created topic
     */
    public String create(String topicName) {
        return getArn(topicName);
    }

    /**
     * Creates a topic in the Amazon Simple Notification Service (SNS) based on the supplied arn (pre configured)
     *
     * @param arn the name of the topic
     * @return the topic Name extracted from the arn.
     */
    public String createFromArn(String arn) {
        final String topicName = arn.substring(arn.lastIndexOf(':') + 1);
        create(topicName);
        return topicName;
    }

    /**
     * Retrieves the Amazon Resource Name (ARN) of a topic in the Amazon Simple Notification Service (SNS) based on the given topic name.
     * The topic name is used to identify the topic for which the ARN is retrieved.
     *
     * @param topicName the name of the topic
     * @return the ARN of the specified topic
     */
    public String getArn(String topicName) {
        return LocalstackSnsConfig.createTopic(sns, topicName).topicArn();
    }

    /**
     * Subscribes a queue to a topic in the Amazon Simple Notification Service (SNS).  Raw message delivery is defaulted
     * to false.
     *
     * @param topicName The name of the topic to subscribe to.
     * @param queueName The name of the queue to subscribe.
     * @return SubscriptionArn of the subscription.  Can be used to unsubscribe.
     * @see #unsubscribe(String)
     */
    public String subscribe(String topicName, String queueName) {
        return subscribe(topicName, queueName, false);
    }

    /**
     * Subscribes a queue to a topic in the Amazon Simple Notification Service (SNS), allowing the queue
     * to receive messages from the specified topic.
     * <p>
     * Queue will be created if it does not exist.
     *
     * @param topicName          The name of the topic to subscribe to.
     * @param queueName          The name of the queue to subscribe.
     * @param rawMessageDelivery Specifies whether the messages received by the subscribed queue should
     *                           be delivered as raw (unprocessed) messages.
     * @return SubscriptionArn of the subscription.  Can be used to unsubscribe.
     * @see #unsubscribe(String)
     */
    public String subscribe(String topicName, String queueName, boolean rawMessageDelivery) {
        final String arn = create(topicName);
        sqs.create(queueName);
        final String queueArn = sqs.getQueueArn(queueName);
        sqs.setSubscribePolicy(queueName, arn);
        return sns.subscribe(r -> r.topicArn(arn)
                                   .endpoint(queueArn)
                                   .protocol("sqs")
                                   .attributes(Map.of("RawMessageDelivery", Boolean.toString(rawMessageDelivery))))
                  .subscriptionArn();
    }

    /**
     * Unsubscribes from a specified Amazon SNS subscription.
     *
     * @param subscriptionArn The ARN of the subscription to unsubscribe from.
     */
    public void unsubscribe(String subscriptionArn) {
        sns.unsubscribe(r -> r.subscriptionArn(subscriptionArn));
    }

    /**
     * Sends a message to the specified topic in the Amazon Simple Notification Service (SNS).
     *
     * @param topicName the name of the topic to send the message to
     * @param body      the content of the message
     */
    public void send(String topicName, String body) {
        convertAndSend(topicName, body);
    }

    /**
     * Sends a message to the specified topic in the Amazon Simple Notification Service (SNS).
     *
     * @param topicName the name of the topic to send the message to
     * @param message   the content of the message
     */
    public void convertAndSend(String topicName, Object message) {
        convertAndSend(topicName, message, null);
    }

    /**
     * Converts and sends a message to the specified topic in the Amazon Simple Notification Service (SNS).
     * The message is converted to JSON format using the provided ObjectMapper.
     *
     * @param topicName       the name of the topic to send the message to
     * @param message         the content of the message to send
     * @param attributeValues a Map containing additional attributes to include in the SNS message,
     *                        where the key is the attribute name and the value is the attribute value
     */
    @SneakyThrows
    public void convertAndSend(String topicName, Object message, Map<String, Object> attributeValues) {
        String json = mapper.writeValueAsString(message);
        sns.publish(b -> {
            b.topicArn(getArn(topicName)).message(json);
            if (attributeValues != null) {
                Map<String, MessageAttributeValue> attributes = new LinkedHashMap<>();
                for (String key : attributeValues.keySet()) {
                    Object attributeValue = attributeValues.get(key);
                    if (attributeValue != null) {
                        String value = attributeValue.toString();
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
                }
                b.messageAttributes(attributes);
            }
        });
    }

    /**
     * Checks if a topic with the given name exists in the list of topics.
     *
     * @param topicName the name of the topic to check for existence
     * @return true if a topic with the given name exists, false otherwise
     */
    public boolean exists(String topicName) {
        return sns.listTopics().topics().stream().anyMatch(t -> t.topicArn().endsWith(topicName));
    }

    /**
     * Deletes an Amazon SNS topic identified by its name.   Associated subscriptions are also deleted.
     *
     * @param topicName the name of the topic to be deleted
     */
    public void destroy(String topicName) {
        final String topicArn = getArn(topicName);
        sns.listSubscriptionsByTopic(r -> r.topicArn(topicArn))
           .subscriptions()
           .forEach(s -> unsubscribe(s.subscriptionArn()));
        sns.deleteTopic(r -> r.topicArn(topicArn));
    }
}
