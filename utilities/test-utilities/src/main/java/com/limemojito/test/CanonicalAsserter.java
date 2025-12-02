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

package com.limemojito.test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assert equals, hashCode and toString.
 */
public class CanonicalAsserter {
    /**
     * Assert equals, hashCode and toString.
     *
     * @param instance  Object to test
     * @param duplicate a duplicate of the test object (same id expected).
     * @param other     a "different" object.
     */
    public static void assertCanonical(Object instance, Object duplicate, Object other) {
        assertToString(instance);

        assertThat(instance).isNotNull();
        assertThat(duplicate).isNotNull();
        assertThat(other).isNotNull();

        //noinspection EqualsWithItself
        assertThat(instance).isEqualTo(instance);
        assertThat(instance).isEqualTo(duplicate);
        assertThat(instance).isNotEqualTo(other);
        assertThat(instance).isNotEqualTo("HF^$*&#2");
        assertThat(instance.hashCode()).isEqualTo(duplicate.hashCode());
    }

    private static void assertToString(Object instance) {
        final String defaultToString = instance.getClass().getName() + "@[0-9a-fA-F]*";
        final String foundToString = instance.toString();
        final boolean matches = Pattern.matches(defaultToString, foundToString);
        assertThat(matches).withFailMessage("Default toString detected [%s]", foundToString).isTrue();
    }
}
