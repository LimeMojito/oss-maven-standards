/*
 * Copyright 2011-2025 Lime Mojito Pty Ltd
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

package com.limemojito.test.s3;

import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.lambda.runtime.serialization.events.serializers.S3EventSerializer;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The S3EventPrototype class provides utility methods for creating and manipulating S3 event notifications.
 * It uses the Freemarker template engine to generate event notifications in JSON format.
 *
 * <p>
 * The S3EventPrototype class has the following public constructors and methods:
 * - {@link #S3EventPrototype()} - Test Utility Constructor. Creates a new S3EventPrototype object using the default S3 event template.
 * - {@link #S3EventPrototype(String)} - Creates a new S3EventPrototype object using a custom notification template.
 * - {@link #createS3Event(String, String, String)} - Generates an S3EventNotification object from the provided bucket name, object key, and event type.
 * - {@link #createS3EventJson(String, String, String)} - Generates a JSON string representation of an S3 event notification using the provided bucket name, object key, and event
 * type.
 * - {@link #fromJson(String)} - Parses a JSON string representation of an S3 event notification into an S3EventNotification object.
 * </p>
 *
 * <p>
 * The S3EventPrototype class is primarily used for test data creation and manipulation of S3 event notifications.
 * It provides a convenient way to generate S3 event notifications for testing purposes.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 * <pre>{@code
 * S3EventPrototype s3EventPrototype = new S3EventPrototype();
 * String bucketName = "my-bucket";
 * String objectKey = "my-object";
 * String eventType = "ObjectCreated:Put";
 *
 * // Generate an S3 event notification
 * S3EventNotification s3Event = s3EventPrototype.createS3Event(bucketName, objectKey, eventType);
 *
 * // Convert the S3 event notification to a JSON string
 * String s3EventJson = s3EventPrototype.createS3EventJson(bucketName, objectKey, eventType);
 *
 * // Parse the JSON string back into an S3EventNotification object
 * S3EventNotification parsedS3Event = s3EventPrototype.fromJson(s3EventJson);
 * }</pre>
 *
 * @see S3EventNotification
 */
@Slf4j
public class S3EventPrototype {
    /**
     * The default S3 template file name for notification events.
     */
    public static final String DEFAULT_S3_TEMPLATE = "notification.ftl";

    private final Template template;
    private final S3EventSerializer<S3EventNotification> mapper;

    /**
     * Test Utility Constructor.  Note this creates a JsonConfig fast mapper, and uses the default S3 Event template.
     *
     * @see #DEFAULT_S3_TEMPLATE
     */
    public S3EventPrototype() {
        this(DEFAULT_S3_TEMPLATE);
    }

    /**
     * @param notificationTemplate template to use for generating events.
     */
    @SneakyThrows
    public S3EventPrototype(String notificationTemplate) {
        this.mapper = new S3EventSerializer<S3EventNotification>().withClass(S3EventNotification.class)
                                                                  .withClassLoader(getClass().getClassLoader());
        final Configuration freemarker = new Configuration(Configuration.VERSION_2_3_23);
        freemarker.setClassForTemplateLoading(S3EventPrototype.class, "/com/limemojito/test/s3");
        log.info("Using template: {}", notificationTemplate);
        this.template = freemarker.getTemplate(notificationTemplate);
    }

    /**
     * This method is exposed for test data creation.  Event time is set to NOW utc.
     *
     * @param bucketName name of bucket to place in template
     * @param objectKey  key to place in template.
     * @param eventType  type of event to create (such as ObjectCreated:Put)
     * @return S3EventNotification object.
     */
    @SneakyThrows
    public S3EventNotification createS3Event(String bucketName, String objectKey, String eventType) {
        String s3EventJson = createS3EventJson(bucketName, objectKey, eventType);
        return mapper.fromJson(s3EventJson);
    }

    /**
     * This method is exposed for test data creation.  Event time is set to NOW utc.
     *
     * @param bucketName name of bucket to place in template
     * @param objectKey  key to place in template.
     * @param eventType  AWS string for the event type.
     * @return S3EventNotification in json format
     * @see S3EventNotification
     */
    @SneakyThrows
    public String createS3EventJson(String bucketName, String objectKey, String eventType) {
        final int initialSize = 8096;
        try (StringWriter stringWriter = new StringWriter(initialSize)) {
            final Map<String, Object> context = new HashMap<>();
            context.put("bucketName", bucketName);
            context.put("objectKey", URLEncoder.encode(objectKey, UTF_8));
            context.put("timeOfEvent", Instant.now(Clock.systemUTC()).toString());
            context.put("eventType", eventType);
            template.process(context, stringWriter);
            stringWriter.flush();
            return stringWriter.toString();
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }

    /**
     * Converts a JSON string representation of an S3 event notification into an S3EventNotification object.
     *
     * @param json The JSON string representation of the S3 event notification.
     * @return An S3EventNotification object representing the parsed JSON data.
     */
    public S3EventNotification fromJson(String json) {
        return mapper.fromJson(json);
    }
}
