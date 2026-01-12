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

package com.limemojito.test;

import lombok.SneakyThrows;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The EnumAsserter class provides a utility method for asserting that an enum class
 * follows certain conventions.
 * <p>
 * To use this class, call the {@code assertEnum} method with the enum class to be
 * asserted. The method checks that the enum's constants, the result of calling its
 * "values" method, and the result of calling its "valueOf" method with each constant's
 * name, are all equal.
 * <p>
 * Example usage:
 *
 * <pre>{@code
 * public enum MyEnum {VALUE1, VALUE2, VALUE3}
 *
 * public class MyEnumTest {
 *     @Test
 *     public void shouldPassAssertEnum() {
 *         EnumAsserter.assertEnum(MyEnum.class);
 *     }
 * }
 * }</pre>
 * <p>
 * Note that this class is not meant to be instantiated, as it only contains a static
 * utility method.
 */
public class EnumAsserter {
    /**
     * The {@code assertEnum} method is a utility method that asserts that an enum class
     * follows certain conventions. It throws exceptions if any of the assertions fail.
     *
     * @param enumClass the class of the enum to be asserted
     * @param <T>       enum type to check.
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> void assertEnum(Class<T> enumClass) {
        T[] constants = enumClass.getEnumConstants();

        Method valuesMethod = enumClass.getMethod("values");
        T[] result = (T[]) valuesMethod.invoke(null);
        assertThat(result).isEqualTo(constants);

        T anEnumConstant = constants[0];
        Method valueOfMethod = enumClass.getMethod("valueOf", String.class);
        assertThat(anEnumConstant).isEqualTo(valueOfMethod.invoke(null, anEnumConstant.name()));
    }
}
