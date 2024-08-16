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

package com.limemojito.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * The ObjectMapperPrototype class provides a utility method for building a customized object mapper.
 * The object mapper is configured to be similar to the global Spring Boot setup.
 */
public class ObjectMapperPrototype {

    private static ObjectMapper instance;

    /**
     * Configures a test object mapper similar to the global spring boot setup.
     * Object mapper is created as a singleton.
     *
     * @return A useful object mapper.
     */
    public static ObjectMapper buildBootLikeMapper() {
        if (instance == null) {
            instance = new ObjectMapper().findAndRegisterModules()
                                         .registerModule(new JavaTimeModule())
                                         .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        return instance;
    }
}
