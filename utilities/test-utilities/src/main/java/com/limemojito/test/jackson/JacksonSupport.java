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

package com.limemojito.test.jackson;

import com.amazonaws.services.lambda.runtime.serialization.PojoSerializer;
import com.amazonaws.services.lambda.runtime.serialization.events.LambdaEventSerializers;
import tools.jackson.core.type.TypeReference;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The {@code JacksonSupport} class provides utility methods for parsing, loading, and converting JSON using the Jackson library.
 */
@Component
@RequiredArgsConstructor
public class JacksonSupport {
    private static final int ONE_KB = 1024;
    private final JsonMapper objectMapper;
    private final Validator validator;
    private final TypeReference<Map<String, Object>> rawMapType = new TypeReference<>() {
    };

    /**
     * Parses and validates JSON String.
     *
     * @param json  Json Data as string
     * @param clazz Type to convert to
     * @param <T>   Typing for conversion
     * @return the object loaded.
     * @throws ConstraintViolationException Runtime if validation constraints fail
     */
    @SneakyThrows
    public <T> T parse(String json, Class<T> clazz) throws ConstraintViolationException {
        T value = objectMapper.readValue(json, clazz);
        return hardValidate(value);
    }

    /**
     * Parses and validates JSON String.
     *
     * @param json Json Data as string
     * @param type Type to convert to (use for Map&lt;, List&lt;, etc)
     * @param <T>  Typing for conversion
     * @return the object loaded.
     * @throws ConstraintViolationException Runtime if validation constraints fail
     */
    @SneakyThrows
    public <T> T parse(String json, TypeReference<T> type) throws ConstraintViolationException {
        T value = objectMapper.readValue(json, type);
        return hardValidate(value);
    }

    /**
     * Parses a Lambda event using the AWS lambda event serializers.   This works around some lambda events having
     * "non-standard" json naming (such as SQSEvent).
     *
     * @param json  json to parse
     * @param clazz Lambda Eventing class to pares.
     * @param <T>   Typing for conversion
     * @return instance of class.
     */
    public <T> T parseLambdaEvent(String json, Class<T> clazz) throws ConstraintViolationException {
        PojoSerializer<T> serializer = lambdaSerializerFor(clazz);
        return serializer.fromJson(json);
    }

    /**
     * Loads and validates JSON object.
     *
     * @param pathResource Where to load from
     * @param clazz        Type to convert to
     * @param <T>          Typing for conversion
     * @return the object loaded.
     * @throws ConstraintViolationException Runtime if validation constraints fail
     */
    @SneakyThrows
    public <T> T load(String pathResource, Class<T> clazz) throws ConstraintViolationException {
        try (InputStream inputStream = loadStream(pathResource)) {
            T value = objectMapper.readValue(inputStream, clazz);
            return hardValidate(value);
        }
    }

    /**
     * Loads and validates JSON object.
     *
     * @param pathResource Where to load from
     * @param type         Type to convert to (use for Map&lt;, List&lt;, etc)
     * @param <T>          Typing for conversion
     * @return the object loaded.
     * @throws ConstraintViolationException Runtime if validation constraints fail
     */
    @SneakyThrows
    public <T> T load(String pathResource, TypeReference<T> type) throws ConstraintViolationException {
        try (InputStream inputStream = loadStream(pathResource)) {
            T value = objectMapper.readValue(inputStream, type);
            return hardValidate(value);
        }
    }

    /**
     * Loads and returns a JSON resource as a list of objects of the specified type.
     *
     * @param pathResource The path of the JSON resource to load.
     * @param clazz        The class of the objects in the list.
     * @param <T>          The type of the objects in the list.
     * @return A list of objects of the specified type loaded from the JSON resource.
     * @throws ConstraintViolationException If validation constraints fail during loading.
     */
    @SneakyThrows
    public <T> List<T> loadAsList(String pathResource, Class<T> clazz) throws ConstraintViolationException {
        try (InputStream inputStream = loadStream(pathResource)) {
            return objectMapper.readValue(inputStream,
                                          objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        }
    }

    /**
     * Loads and returns a Lambda event object from the specified JSON resource. This method reads the JSON resource as
     * an {@code InputStream}, parses it into a {@code String}, and then converts it into the specified Lambda event object
     * using the given class.
     *
     * @param pathResource The path of the JSON resource to load.
     * @param clazz        The class representing the Lambda event object.
     * @param <T>          The type of the Lambda event object.
     * @return The loaded Lambda event object.
     * @throws ConstraintViolationException If validation constraints fail during loading.
     */
    @SneakyThrows
    public <T> T loadLambdaEvent(String pathResource, Class<T> clazz) throws ConstraintViolationException {
        try (InputStream inputStream = loadStream(pathResource)) {
            String jsonString = IOUtils.toString(inputStream, UTF_8);
            return parseLambdaEvent(jsonString, clazz);
        }
    }

    /**
     * Convert a JsonString to a map
     *
     * @param jsonValue Plain Old Java Object to convert
     * @return map format json.
     */
    @SneakyThrows
    public Map<String, Object> toRaw(String jsonValue) {
        return objectMapper.readValue(jsonValue, rawMapType);
    }

    /**
     * Convert a POJO to a map
     *
     * @param pojo Plain Old Java Object to convert
     * @return map format json.
     */
    public Map<String, Object> toRaw(Object pojo) {
        return objectMapper.convertValue(pojo, rawMapType);
    }


    /**
     * Converts an object to its JSON representation.
     *
     * @param pojo the object to be converted to JSON
     * @return the JSON representation of the object
     */
    @SneakyThrows
    public String toJson(Object pojo) {
        return objectMapper.writeValueAsString(pojo);
    }

    /**
     * Converts an object to its JSON representation.
     *
     * @param lambdaEventInstance The object to be converted to JSON.
     * @param <T>                 The type of the object.
     * @return The JSON representation of the object.
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> String toJsonLambdaEvent(T lambdaEventInstance) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(ONE_KB)) {
            Class<T> aClass = (Class<T>) lambdaEventInstance.getClass();
            PojoSerializer<T> pojoSerializer = lambdaSerializerFor(aClass);
            pojoSerializer.toJson(lambdaEventInstance, outputStream);
            return outputStream.toString(UTF_8);
        }
    }

    /**
     * Note this does strict JSON type checking - make sure inputs are boolean, number, etc.
     *
     * @param found    Pojo found
     * @param expected pojo expected
     * @see JsonAssertions#assertThatJson
     */
    public void assertJson(Object found, Object expected) {
        assertThatJson(found).isEqualTo(expected);
    }

    private <T> T hardValidate(T value) {
        Set<ConstraintViolation<T>> violations = validator.validate(value);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return value;
    }

    private InputStream loadStream(String pathResource) {
        InputStream resourceAsStream = getClass().getResourceAsStream(pathResource);
        assertThat(resourceAsStream).withFailMessage("Could not load %s from classpath".formatted(pathResource))
                                    .isNotNull();
        return resourceAsStream;
    }

    private <T> PojoSerializer<T> lambdaSerializerFor(Class<T> clazz) {
        return LambdaEventSerializers.serializerFor(clazz, this.getClass().getClassLoader());
    }
}
