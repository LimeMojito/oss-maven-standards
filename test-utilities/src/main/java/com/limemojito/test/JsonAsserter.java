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

package com.limemojito.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonAsserter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonAsserter.class);

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperPrototype.buildBootLikeMapper();
    private static final JsonAsserter INSTANCE = new JsonAsserter(OBJECT_MAPPER);
    private final ObjectMapper objectMapper;

    public JsonAsserter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static void assertSerializeDeserialize(Object obj) throws IOException {
        INSTANCE.assertSerializeAndDeserialize(obj);
    }

    public void assertSerializeAndDeserialize(Object obj) throws IOException {
        String json = objectMapper.writeValueAsString(obj);
        LOGGER.info("JSON is {}", json);
        Class<?> clazz = obj.getClass();
        Object object = objectMapper.readValue(json, clazz);
        assertThat(object).isInstanceOf(clazz);
        assertThat(object).withFailMessage(format("Expected deserialized object %s to be equal to original %s",
                                                  object,
                                                  obj))
                          .isEqualTo(obj);
    }
}
