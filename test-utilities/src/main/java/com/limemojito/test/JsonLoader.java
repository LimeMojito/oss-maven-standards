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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonLoader {

    private final ObjectMapper objectMapper;

    public JsonLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T loadFrom(String resourcePath, Class<T> aClass) throws IOException {
        try (InputStream resourceAsStream = getClass().getResourceAsStream(resourcePath)) {
            assertThat(resourceAsStream).withFailMessage("Could not load " + resourcePath).isNotNull();
            return objectMapper.readValue(resourceAsStream, aClass);
        }
    }

    public <T> T loadFrom(String resourcePath, TypeReference<T> typeReference) throws IOException {
        try (InputStream resourceAsStream = getClass().getResourceAsStream(resourcePath)) {
            assertThat(resourceAsStream).withFailMessage("Could not load " + resourcePath).isNotNull();
            return objectMapper.readValue(resourceAsStream, typeReference);
        }
    }
}
