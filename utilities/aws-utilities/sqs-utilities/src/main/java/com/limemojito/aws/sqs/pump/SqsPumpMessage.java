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

package com.limemojito.aws.sqs.pump;

import lombok.Value;

import java.util.Map;

/**
 * The {@code SqsPumpMessage} class represents a message received from an Amazon Simple Queue Service (SQS) queue.
 * Each instance of this class contains the actual message payload and optional attributes associated with the message.
 *
 * <p>
 * This class is immutable and follows the value object pattern. Once created, the values of the {@code SqsPumpMessage}
 * instance cannot be modified.
 * </p>
 *
 * <p>
 * The message payload is stored in the {@code message} field, which can hold any Java object. The optional attributes
 * associated with the message are stored in the {@code attributes} field, which is a map of string keys to arbitrary
 * object values.
 * </p>
 *
 *
 * <h2>Usage:</h2>
 *
 * <pre>
 * SqsPumpMessage message = new SqsPumpMessage(payload, attributes);
 * </pre>
 *
 * <h2>Serialization:</h2>
 *
 * <p>
 * This class is annotated with Lombok's {@code @Value} annotation, and therefore provides both {@code equals()},
 * {@code hashCode()}, and {@code toString()} methods based on the values of its fields.
 * </p>
 *
 * <p>
 * To serialize/deserialize instances of this class, a framework such as JSON or XML serialization can be used.
 * </p>
 */
@Value
@SuppressWarnings("RedundantModifiersValueLombok")
public class SqsPumpMessage {
    private Object message;
    private Map<String, Object> attributes;
}
