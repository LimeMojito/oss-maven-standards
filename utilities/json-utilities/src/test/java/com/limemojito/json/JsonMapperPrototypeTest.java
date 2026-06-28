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

import org.junit.jupiter.api.Test;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonMapperPrototypeTest {

    @Test
    public void shouldHavePrivateConstructor() throws Exception {
        Constructor<JsonMapperPrototype> constructor = JsonMapperPrototype.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void shouldBuildBootLikeMapper() {
        JsonMapper mapper = JsonMapperPrototype.buildBootLikeMapper();
        assertThat(mapper).isNotNull();
    }

    @Test
    public void shouldBuildCustomMapper() {
        JsonMapper mapper = JsonMapperPrototype.buildMapper(builder -> builder.configure(SerializationFeature.INDENT_OUTPUT, true));
        assertThat(mapper).isNotNull();
        assertThat(mapper.isEnabled(SerializationFeature.INDENT_OUTPUT)).isTrue();
    }
}
