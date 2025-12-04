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

package com.limemojito.test.aws.integration;

import com.limemojito.test.sns.SnsSupport;
import com.limemojito.test.sqs.SqsSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.services.sqs.model.Message;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("integration-test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ITConfig.class, initializers = ConfigDataApplicationContextInitializer.class)
public class SnsSupportIT {
    @Value("${aws.test.sns.topic}")
    private String topicName;

    @Autowired
    private SnsSupport snsSupport;

    @Autowired
    private SqsSupport sqsSupport;
    private String qName;
    private String subArn;


    @BeforeEach
    public void setup() {
        snsSupport.create(topicName);
        assertThat(snsSupport.exists(topicName)).isTrue();
        qName = "testQSubscribe";
        sqsSupport.create(qName);
        assertThat(sqsSupport.exists(qName)).isTrue();
        sqsSupport.purge(qName);
        subArn = snsSupport.subscribe(topicName, qName, true);
    }

    @AfterEach
    public void tearDown() {
        snsSupport.unsubscribe(subArn);
        sqsSupport.destroy(qName);
        assertThat(sqsSupport.exists(qName)).isFalse();
        snsSupport.destroy(topicName);
        assertThat(snsSupport.exists(topicName)).isFalse();
    }

    @Test
    public void shouldPutDataAsString() {
        assertThat(sqsSupport.getQueueCount(qName)).isEqualTo(0);

        snsSupport.send(topicName, "hello world");

        Message message1 = sqsSupport.waitForMessage(qName);
        assertThat(message1.body()).isEqualTo("\"hello world\"");
    }
}
