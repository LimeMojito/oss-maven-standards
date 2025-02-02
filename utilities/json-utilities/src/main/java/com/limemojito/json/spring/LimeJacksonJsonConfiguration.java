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

package com.limemojito.json.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limemojito.json.JsonLoader;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Directly all the JacksonAutoConfiguration to solve issues in Intellij with bean detection.
 *
 * @see JacksonAutoConfiguration
 */
@Configuration
@Import(JacksonAutoConfiguration.class)
public class LimeJacksonJsonConfiguration {

    /**
     * Creates a new json loader delegating to the supplied object mapper.
     *
     * @param objectMapper Jackson objet mapper to delegate JSON parsing to.
     * @return a json loader bean.
     */
    @Bean
    public JsonLoader jsonLoader(ObjectMapper objectMapper) {
        return new JsonLoader(objectMapper);
    }
}
