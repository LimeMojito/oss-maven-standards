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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limemojito.json.ObjectMapperPrototype;
import com.limemojito.test.s3.S3SupportConfig;
import com.limemojito.test.sns.SnsSupportConfig;
import com.limemojito.test.sqs.SqsSupportConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/test.properties")
@TestConfiguration
@Import({S3SupportConfig.class, SnsSupportConfig.class, SqsSupportConfig.class})
class ITConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return ObjectMapperPrototype.buildBootLikeMapper();
    }
}
