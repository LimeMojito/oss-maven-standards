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

import com.limemojito.test.s3.S3Support;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("integration-test")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ITConfig.class, initializers = ConfigDataApplicationContextInitializer.class)
public class S3SupportIT {

    @Value("${aws.test.s3.bucket}")
    private String bucketName;

    @Autowired
    private S3Support s3Support;

    @Before
    public void cleanBucket() {
        s3Support.wipeBucket(bucketName);
    }

    @Test
    public void shouldPutData() throws Exception {
        final String message = "Hello World";
        final byte[] bytes = message.getBytes(UTF_8);
        final String key = "test/somefile.txt";
        s3Support.putData(bucketName, key, new ByteArrayInputStream(bytes), bytes.length, "text/plain");
        assertThat(s3Support.keyExists(bucketName, key)).isTrue();
        try (InputStream data = s3Support.getData(bucketName, key)) {
            assertThat(IOUtils.toString(data, UTF_8)).isEqualTo(message);
        }
    }
}
