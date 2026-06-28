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

package com.limemojito.json;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;


/**
 * A utility class for loading JSON data from a resource using the Jackson library.
 * Uses an instance of ObjectMapper to perform the deserialization.  Exceptions are converted to runtime
 * to assist with functional style programming.
 */
public class JsonLoader {

    /**
     * A type reference to use to convert to a Map object.
     */
    public static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final JsonMapper jsonMapper;

    /**
     * Creates a new JSON loader.
     *
     * @param jsonMapper Jackson object mapper to delegate JSON parsing to.
     */
    public JsonLoader(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    /**
     * Parses the supplied string json to an object value.
     *
     * @param json  String json to convert.
     * @param clazz Class type to convert to.
     * @param <T>   Instance type expected.
     * @return Instance of the supplied class.
     */
    public <T> T convert(String json, Class<T> clazz) {
        return jsonMapper.readValue(json, clazz);
    }

    /**
     * Parses the supplied string json to an object value.
     *
     * @param json  byte array of json to convert (Assuming UTF-8).
     * @param clazz Class type to convert to.
     * @param <T>   Instance type expected.
     * @return Instance of the supplied class.
     */
    public <T> T convert(byte[] json, Class<T> clazz) {
        return jsonMapper.readValue(json, clazz);
    }

    /**
     * Parses the supplied input stream as json to an object value.
     *
     * @param json  InputStream of json to convert.
     * @param clazz Class type to convert to.
     * @param <T>   Instance type expected.
     * @return Instance of the supplied class.
     */
    public <T> T convert(InputStream json, Class<T> clazz) {
        return jsonMapper.readValue(json, clazz);
    }

    /**
     * Parses the supplied string json to an object value.
     *
     * @param json          String json to convert.
     * @param typeReference Jackson compatible type to convert to.
     * @param <T>           Instance type expected.
     * @return Instance of the supplied class.
     */
    public <T> T convert(String json, TypeReference<T> typeReference) {
        return jsonMapper.readValue(json, typeReference);
    }

    /**
     * Parses the supplied input stream as json to an object value.
     *
     * @param json          InputStream of json to convert.
     * @param typeReference Jackson compatible type to convert to.
     * @param <T>           Instance type expected.
     * @return Instance of the supplied class.
     */
    public <T> T convert(InputStream json, TypeReference<T> typeReference) {
        return jsonMapper.readValue(json, typeReference);
    }

    /**
     * Parses the supplied string json to an object value.
     *
     * @param json          byte array of json to convert (Assuming UTF-8).
     * @param typeReference Jackson compatible type to convert to.
     * @param <T>           Instance type expected.
     * @return Instance of the supplied class.
     */
    public <T> T convert(byte[] json, TypeReference<T> typeReference) {
        return jsonMapper.readValue(json, typeReference);
    }

    /**
     * Converts the supplied JSON to a map
     *
     * @param json json to convert
     * @return JSON as map object.
     */
    public Map<String, Object> convertToMap(String json) {
        return convert(json, MAP_TYPE);
    }

    /**
     * Converts the supplied JSON to a map
     *
     * @param instance Object instance to convert
     * @return JSON as map object.
     */
    public Map<String, Object> convertToMap(Object instance) {
        return jsonMapper.convertValue(instance, MAP_TYPE);
    }

    /**
     * Converts an object instance to a Json string.
     *
     * @param instance Instance to convert.
     * @return String in json format.
     */
    public String toJson(Object instance) {
        return jsonMapper.writeValueAsString(instance);
    }

    /**
     * Writes an object instance to a Json string to the output stream.
     *
     * @param output   Output Stream to write to.
     * @param instance Instance to convert.
     */
    public void toJson(OutputStream output, Object instance) {
        jsonMapper.writeValue(output, instance);
    }

    /**
     * Writes an object instance to a Json string to the output stream.
     *
     * @param outputWriter Writer to write to.
     * @param instance     Instance to convert.
     */
    public void toJson(Writer outputWriter, Object instance) {
        jsonMapper.writeValue(outputWriter, instance);
    }

    /**
     * Load JSON data from a resource using the Jackson library and deserialize it into an object of the specified class.
     *
     * @param resourcePath The path of the resource from which to load the JSON data.
     * @param aClass       The Class object representing the type of the target object.
     * @param <T>          The type of the target object.
     * @return The deserialized object of the specified class.
     */
    public <T> T loadFrom(String resourcePath, Class<T> aClass) {
        return loadFrom(resourcePath, (stream) -> jsonMapper.readValue(stream, aClass));
    }

    /**
     * Load JSON data from a resource using the Jackson library and deserialize it into an object of the specified type.
     *
     * @param resourcePath  The path of the resource from which to load the JSON data.
     * @param typeReference The TypeReference object representing the type of the target object.
     * @param <T>           The type of the target object.
     * @return The deserialized object of the specified type.
     */
    public <T> T loadFrom(String resourcePath, TypeReference<T> typeReference) {
        return loadFrom(resourcePath, (stream) -> jsonMapper.readValue(stream, typeReference));
    }

    /**
     * Loads a resource as a stream.
     *
     * @param resourcePath Path to the resource.
     * @return The resource stream.
     */
    protected InputStream getResourceAsStream(String resourcePath) {
        return getClass().getResourceAsStream(resourcePath);
    }

    private <T> T loadFrom(String resourcePath, Parser<T> parser) {
        InputStream resourceAsStream = getResourceAsStream(resourcePath);
        assertStreamFound(resourcePath, resourceAsStream);
        try {
            T result = parser.parse(resourceAsStream);
            resourceAsStream.close();
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void assertStreamFound(String resourcePath, InputStream resourceAsStream) {
        if (resourceAsStream == null) {
            throw new IllegalArgumentException("Could not load %s from classpath.".formatted(resourcePath));
        }
    }

    @FunctionalInterface
    private interface Parser<T> {
        T parse(InputStream stream) throws IOException;
    }
}
