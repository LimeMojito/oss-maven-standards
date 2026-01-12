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

package com.limemojito.json.spring;

import com.limemojito.json.JsonLoader;
import com.limemojito.json.JsonMapperPrototype;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

/**
 * Configures the Lime JasonLoader using Jackson in Spring Boot.
 */
@Configuration
@Slf4j
public class LimeJacksonJsonConfiguration {

    /**
     * If there is no JsonMapper defined, we create a default one using JsonMapperPrototype#buildBootLikeMapper().
     *
     * @return a valid JsonMapper.
     * @see JsonMapperPrototype#buildBootLikeMapper()
     */
    @ConditionalOnMissingBean(JsonMapper.class)
    @Bean
    public JsonMapper limeJsonMapper() {
        log.warn("No Jackson JsonMapper found, creating a default one using JsonMapperPrototype#buildBootLikeMapper().");
        return JsonMapperPrototype.buildBootLikeMapper();
    }

    /**
     * Creates a new Lime JSON loader delegating to the supplied object mapper.
     *
     * @param objectMapper Jackson object mapper to delegate JSON parsing to.
     * @return a json loader bean.
     */
    @Bean
    public JsonLoader jsonLoader(JsonMapper objectMapper) {
        return new JsonLoader(objectMapper);
    }
}
