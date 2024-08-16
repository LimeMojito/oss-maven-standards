/*
 * Copyright 2011-2024 Lime Mojito Pty Ltd
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

package com.limemojito.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limemojito.json.ObjectMapperPrototype;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks that an instance can be serialized and deserialized from JSON
 */
@Slf4j
public class JsonAsserter {
    private static final JsonAsserter INSTANCE = new JsonAsserter(ObjectMapperPrototype.buildBootLikeMapper());
    private final ObjectMapper objectMapper;

    /**
     * Represents a class that checks whether an instance can be serialized and deserialized from JSON.
     *
     * @param objectMapper Jackson object mapper to use.
     */
    public JsonAsserter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Checks that an instance can be serialized and deserialized from JSON
     *
     * @param obj instance to check
     */
    public static void assertSerializeDeserialize(Object obj) {
        INSTANCE.assertSerializeAndDeserialize(obj);
    }

    /**
     * Checks that an instance can be serialized and deserialized from JSON
     *
     * @param obj instance to check
     */
    @SneakyThrows
    public void assertSerializeAndDeserialize(Object obj) {
        String json = objectMapper.writeValueAsString(obj);
        log.info("JSON is {}", json);
        Class<?> clazz = obj.getClass();
        Object object = objectMapper.readValue(json, clazz);
        assertThat(object).isInstanceOf(clazz);
        assertThat(object).withFailMessage(format("Expected deserialized object %s to be equal to original %s",
                                                  object,
                                                  obj))
                          .isEqualTo(obj);
    }
}
