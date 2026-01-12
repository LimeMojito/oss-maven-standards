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

import com.limemojito.test.s3.S3Support;
import com.limemojito.test.s3.S3SupportConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test is here to check that unit test support for docker functions as expected.  We set the spring profile to
 * "integration-test" so that the existing test utilities behave as expected.
 */
@ActiveProfiles("integration-test")
@SpringBootTest(classes = S3SupportConfig.class)
public class S3DockerUnitTest {

    @Autowired
    private S3Support s3;
    private URI s3Uri;

    @BeforeEach
    void setUp() {
        s3Uri = s3.toS3Uri("unit-test-bucket", "test.txt");
        s3.wipeBucket(s3Uri);
    }

    @Test
    public void shouldDoThingsWithS3AsAUnitTest() {
        s3.putData(s3Uri, "text/plain", "hello world".getBytes(UTF_8));
        assertThat(s3.keyExists(s3Uri)).withFailMessage("Key %s is missing", s3Uri)
                                       .isTrue();
    }
}
