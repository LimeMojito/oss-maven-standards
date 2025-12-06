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


import tools.jackson.databind.json.JsonMapper;

import java.util.function.Consumer;

/**
 * The JsonMapperPrototype class provides a utility methods for building a customized object mapper.
 */
public class JsonMapperPrototype {

    /**
     * Configures a test object mapper similar to the default Jackson 3 setup.  Modules are loaded automatically.
     *
     * @return A useful object mapper.
     */
    public static JsonMapper buildBootLikeMapper() {
        return buildMapper(b -> {
        });
    }

    /**
     * Configures a test object mapper with customization options applied after Jackson module loads.
     *
     * @param customizer Customization options to apply.
     * @return A useful object mapper.
     */
    public static JsonMapper buildMapper(Consumer<JsonMapper.Builder> customizer) {
        final JsonMapper.Builder builder = JsonMapper.builder();
        builder.findAndAddModules();
        customizer.accept(builder);
        return builder.build();
    }
}
