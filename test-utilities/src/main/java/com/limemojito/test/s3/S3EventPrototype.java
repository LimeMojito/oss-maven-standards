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

package com.limemojito.test.s3;

import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.lambda.runtime.serialization.events.serializers.S3EventSerializer;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class S3EventPrototype {
    public static final String DEFAULT_S3_TEMPLATE = "notification.ftl";
    private static final Logger LOGGER = LoggerFactory.getLogger(S3EventPrototype.class);
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
    public S3EventPrototype(String notificationTemplate) {
        try {
            this.mapper = new S3EventSerializer<S3EventNotification>().withClass(S3EventNotification.class)
                                                                      .withClassLoader(getClass().getClassLoader());
            final Configuration freemarker = new Configuration(Configuration.VERSION_2_3_23);
            freemarker.setClassForTemplateLoading(S3EventPrototype.class, "/com/limemojito/test/s3");
            LOGGER.info("Using template: {}", notificationTemplate);
            this.template = freemarker.getTemplate(notificationTemplate);
        } catch (IOException e) {
            throw new RuntimeException("Could not load template", e);
        }
    }

    /**
     * This method is exposed for test data creation.  Event time is set to NOW utc.
     *
     * @param bucketName name of bucket to place in template
     * @param objectKey  key to place in template.
     * @param eventType  type of event to create (such as ObjectCreated:Put)
     * @return S3EventNotification object.
     * @throws IOException on an event creation failure.
     */
    public S3EventNotification createS3Event(String bucketName, String objectKey, String eventType) throws IOException {
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
     * @throws IOException on an event creation failure.
     * @see S3EventNotification
     */
    public String createS3EventJson(String bucketName, String objectKey, String eventType) throws IOException {
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

    public S3EventNotification fromJson(String json) {
        return mapper.fromJson(json);
    }
}
