/*
 * Copyright 2011-2023 Lime Mojito Pty Ltd
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

import org.junit.Test;

import java.io.NotSerializableException;

public class CacheableAsserterTest {

    @Test
    public void shouldSerializeOk() throws Exception {
        CacheableAsserter.assertCacheable("This is a simple string");
    }

    @Test(expected = NotSerializableException.class)
    public void shouldNotBeSerializable() throws Exception {
        CacheableAsserter.assertCacheable(new Wombat("bang"));
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void shouldCoverConstructor() {
        new CacheableAsserter();
    }

    private static class Wombat {
        private final String value;

        public Wombat(String notDefaultConstructor) {
            value = notDefaultConstructor;
        }

        public String getValue() {
            return value;
        }
    }
}
