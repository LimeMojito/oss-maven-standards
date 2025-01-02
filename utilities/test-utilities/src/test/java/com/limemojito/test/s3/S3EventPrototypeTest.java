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
import freemarker.template.TemplateNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class S3EventPrototypeTest {

    private final S3EventPrototype s3EventPrototype = new S3EventPrototype();

    private final String bucket = "aBucket";
    private final String key = "aKey";
    private final String event = "S3::PutObject";

    @Test
    public void shouldGenerateS3Event() {
        final S3EventNotification s3Event = s3EventPrototype.createS3Event(this.bucket, this.key, this.event);

        assertSingleEventNotification(this.bucket, this.key, this.event, s3Event);
    }

    @Test
    public void shouldGenerateS3EventJson() {
        String json = s3EventPrototype.createS3EventJson(bucket, key, event);

        final S3EventNotification s3EventNotification = s3EventPrototype.fromJson(json);

        assertSingleEventNotification(this.bucket, this.key, this.event, s3EventNotification);
    }

    @Test(expected = TemplateNotFoundException.class)
    public void shouldFailLoad() {
        new S3EventPrototype("missingTemplate");
    }

    private void assertSingleEventNotification(String bucket, String key, String event, S3EventNotification s3Event) {
        Assertions.assertThat(s3Event.getRecords()).hasSize(1);

        final S3EventNotification.S3EventNotificationRecord record = s3Event.getRecords().get(0);
        final S3EventNotification.S3Entity s3Entity = record.getS3();
        final S3EventNotification.S3BucketEntity bucketEntity = s3Entity.getBucket();
        final S3EventNotification.S3ObjectEntity objectEntity = s3Entity.getObject();

        Assertions.assertThat(bucketEntity.getName()).isEqualTo(bucket);
        Assertions.assertThat(bucketEntity.getArn()).isEqualTo("arn:aws:s3:::" + bucket);
        Assertions.assertThat(objectEntity.getKey()).isEqualTo(key);
        Assertions.assertThat(record.getEventName()).isEqualTo(event);
    }
}
