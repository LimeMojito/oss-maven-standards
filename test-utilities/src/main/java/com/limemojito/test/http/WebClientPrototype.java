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

package com.limemojito.test.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class WebClientPrototype {

    public static WebClient.Builder builder(String baseUrl, ObjectMapper objectMapper) {
        final Jackson2JsonEncoder encoder = new Jackson2JsonEncoder(objectMapper, APPLICATION_JSON);
        final Jackson2JsonDecoder decoder = new Jackson2JsonDecoder(objectMapper, APPLICATION_JSON);
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                                                                .codecs(configurer -> {
                                                                    configurer.defaultCodecs()
                                                                              .jackson2JsonEncoder(encoder);
                                                                    configurer.defaultCodecs()
                                                                              .jackson2JsonDecoder(decoder);
                                                                }).build();
        return WebClient.builder()
                        .baseUrl(baseUrl)
                        .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .exchangeStrategies(strategies);
    }
}
