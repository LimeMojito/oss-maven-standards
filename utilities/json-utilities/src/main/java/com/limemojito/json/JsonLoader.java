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

package com.limemojito.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.InputStream;


/**
 * A utility class for loading JSON data from a resource using the Jackson library.
 * Uses an instance of ObjectMapper to perform the deserialization.  Exceptions are converted to runtime
 * to assist with functional style programming.
 */
public class JsonLoader {

    private final ObjectMapper objectMapper;

    /**
     * A utility class for loading JSON data from a resource using the Jackson library.
     * Uses an instance of ObjectMapper to perform the deserialization.
     *
     * @param objectMapper Jackson Object mapper to delegate JSON operations to.
     */
    public JsonLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Parses the supplied string json to an object value.
     *
     * @param json  String json to convert
     * @param clazz Class type to convert to
     * @param <T>   Instance type expected.
     * @return Instance of the supplied class.
     */
    @SneakyThrows
    public <T> T convert(String json, Class<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * Parses the supplied string json to an object value.
     *
     * @param json          String json to convert
     * @param typeReference Jackson compatible type to convert to
     * @param <T>           Instance type expected.
     * @return Instance of the supplied class.
     */
    @SneakyThrows
    public <T> T convert(String json, TypeReference<T> typeReference) {
        return objectMapper.readValue(json, typeReference);
    }

    /**
     * Load JSON data from a resource using the Jackson library and deserialize it into an object of the specified class.
     *
     * @param resourcePath The path of the resource from which to load the JSON data.
     * @param aClass       The Class object representing the type of the target object.
     * @param <T>          The type of the target object.
     * @return The deserialized object of the specified class.
     */
    @SneakyThrows
    public <T> T loadFrom(String resourcePath, Class<T> aClass) {
        try (InputStream resourceAsStream = getClass().getResourceAsStream(resourcePath)) {
            assertStreamFound(resourcePath, resourceAsStream);
            return objectMapper.readValue(resourceAsStream, aClass);
        }
    }

    /**
     * Load JSON data from a resource using the Jackson library and deserialize it into an object of the specified type.
     *
     * @param resourcePath  The path of the resource from which to load the JSON data.
     * @param typeReference The TypeReference object representing the type of the target object.
     * @param <T>           The type of the target object.
     * @return The deserialized object of the specified type.
     */
    @SneakyThrows
    public <T> T loadFrom(String resourcePath, TypeReference<T> typeReference) {
        try (InputStream resourceAsStream = getClass().getResourceAsStream(resourcePath)) {
            assertStreamFound(resourcePath, resourceAsStream);
            return objectMapper.readValue(resourceAsStream, typeReference);
        }
    }

    private static void assertStreamFound(String resourcePath, InputStream resourceAsStream) {
        if (resourceAsStream == null) {
            throw new IllegalArgumentException("Could not load %s from classpath.".formatted(resourcePath));
        }
    }
}
