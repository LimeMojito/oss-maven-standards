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

package com.limemojito.test.http;

import com.limemojito.json.JsonMapperPrototype;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * The WebClientPrototype class provides a utility method for creating a configured WebClient.Builder object.
 * This builder can be used to build a WebClient with a specified base URL and object mapper and is useful outside a
 * spring boot container for testing.
 *
 * @see JsonMapperPrototype
 */
public class WebClientPrototype {

    /**
     * Returns a WebClient.Builder object with the specified base URL and object mapper.
     *
     * @param baseUrl      the base URL for the WebClient
     * @param objectMapper the object mapper to be used for serialization and deserialization
     * @return a WebClient.Builder object configured with the specified base URL and object mapper
     * @see JsonMapperPrototype
     */
    public static WebClient.Builder builder(String baseUrl, JsonMapper objectMapper) {
        final JacksonJsonEncoder encoder = new JacksonJsonEncoder(objectMapper, APPLICATION_JSON);
        final JacksonJsonDecoder decoder = new JacksonJsonDecoder(objectMapper, APPLICATION_JSON);
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                                                                .codecs(configurer -> {
                                                                    configurer.defaultCodecs()
                                                                              .jacksonJsonEncoder(encoder);
                                                                    configurer.defaultCodecs()
                                                                              .jacksonJsonDecoder(decoder);
                                                                }).build();
        return WebClient.builder()
                        .baseUrl(baseUrl)
                        .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .exchangeStrategies(strategies);
    }
}
