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
import com.limemojito.aws.sqs.SqsSenderConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The SqsPumpConfig class is a configuration class for the SqsPump component.
 * It is responsible for creating and configuring the SqsPump bean.
 * <p>
 * Usage:
 * SqsPumpConfig can be used as a configuration class in a Spring Boot application by
 * annotating it with the @Configuration annotation. By default, it will scan for the SqsPump
 * component in the base package classes specified in the @ComponentScan annotation.
 * <p>
 * Dependencies:
 * - SqsAsyncClient: The SQS client used for interacting with the SQS service.
 * - ObjectMapper: The ObjectMapper used for serialization and deserialization of SQS messages.
 * <p>
 * Configuration Properties:
 * pumpMaxBatchSize: The maximum number of messages to send in a single batch from SQS.
 * <p>
 * Flush Behavior:
 * The SqsPumpConfig class provides methods for sending messages to a destination and for flushing
 * messages to a specified destination. It supports flushing a single destination or flushing all
 * destinations maintained in the localPump object. The flushAll() method should be called before
 * destroying the SqsPumpConfig object to ensure that any pending data in the destinations is flushed.
 * <p>
 * Message Serialization:
 * The SqsPumpConfig class uses the ObjectMapper provided in the constructor to serialize the message
 * objects to JSON before sending them. It also sets the headers for ID, contentType, and Timestamp
 * (epoch millis) to ensure compatibility with Spring messaging. The headers can be customized by
 * providing additional attributes in the send() method.
 * <p>
 * Deduplication:
 * The SqsPumpConfig class provides constants for the header names used for deduplication ID and message group ID.
 * These headers can be included in the attributes parameter of the send() method to control deduplication behavior.
 * <p>
 * Threading:
 * The SqsPump component is designed to be used in a multithreaded context with exclusive flush semantics. It uses
 * a ConcurrentHashMap to store messages for each destination, allowing multiple threads to send messages at the same time.
 * However, when flushing messages to a destination, the flush() method is synchronized to ensure that only one thread
 * flushes messages at a time.
 * <p>
 * Closeable:
 * The SqsPumpConfig class implements the AutoCloseable interface. It overrides the close() method to flush any pending
 * data and release any resources held by the object. It is important to call close() before destroying the object to
 * ensure that any pending data is written to the destinations.
 */
@Configuration
@Import(SqsSenderConfig.class)
@ComponentScan(basePackageClasses = SqsPump.class)
public class SqsPumpConfig {
}
