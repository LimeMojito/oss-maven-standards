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

package com.limemojito.test.jackson;

import com.amazonaws.services.lambda.runtime.serialization.PojoSerializer;
import com.amazonaws.services.lambda.runtime.serialization.events.LambdaEventSerializers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@Component
@RequiredArgsConstructor
public class JacksonSupport {
    private static final int ONE_KB = 1024;
    private final ObjectMapper objectMapper;
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
     * @param type Type to convert to (use for Map<, List<, etc)
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
     * @param type         Type to convert to (use for Map<, List<, etc)
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

    @SneakyThrows
    public <T> List<T> loadAsList(String pathResource, Class<T> clazz) throws ConstraintViolationException {
        try (InputStream inputStream = loadStream(pathResource)) {
            return objectMapper.readValue(inputStream,
                                          objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        }
    }

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


    @SneakyThrows
    public String toJson(Object pojo) {
        return objectMapper.writeValueAsString(pojo);
    }

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
