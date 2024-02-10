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

import org.hamcrest.core.Is;

import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertFalse;

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

        assertThat(instance, not(Is.is(nullValue())));
        assertThat(duplicate, not(Is.is(nullValue())));
        assertThat(other, not(Is.is(nullValue())));

        assertThat(instance, Is.is(instance));
        assertThat(instance, Is.is(duplicate));
        assertThat(instance.equals(other), Is.is(false));
        assertThat(instance.equals("HF^$*&#2"), Is.is(false));
        assertThat(instance.hashCode(), Is.is(duplicate.hashCode()));
    }

    private static void assertToString(Object instance) {
        final String defaultToString = instance.getClass().getName() + "@[0-9a-fA-F]*";
        final String foundToString = instance.toString();
        final boolean matches = Pattern.matches(defaultToString, foundToString);
        assertFalse(String.format("Default toString detected [%s]", foundToString), matches);
    }
}
