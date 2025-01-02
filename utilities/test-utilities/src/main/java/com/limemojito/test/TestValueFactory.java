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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Creates a test value for a given class.
 */
public final class TestValueFactory {

    private final Map<Class<?>, Object> instanceStore;

    /**
     * Registers a test instance value for a given class so you can work with those types.
     *
     * @param clazz    The class for which to register the test instance.
     * @param instance The test instance to register.
     */
    public void registerTestInstanceFor(Class<?> clazz, Object instance) {
        instanceStore.put(clazz, instance);
    }

    /**
     * Creates a test value for a supplied type. All primitives and boxes are supported, unknown classes
     * are instantiated via a call to newInstance().
     *
     * @param type Type of class to create test value for.
     * @return a value for the supplied type.
     */
    public Object createFor(Class<?> type) {
        Object rv = null;
        if (type.isEnum()) {
            rv = type.getEnumConstants()[0];
        }
        if (rv == null) {
            rv = instanceStore.get(type);
        }
        if (rv == null) {
            // fallback on a create if we have no intercepts.
            rv = instatiateClass(type);
        }
        return rv;
    }

    /**
     * Create a new factory.
     */
    public TestValueFactory() {
        this.instanceStore = new HashMap<>();
        registerTestInstanceFor(UUID.class, UUID.randomUUID());
        registerTestInstanceFor(BigDecimal.class, new BigDecimal("99.999"));
        registerTestInstanceFor(BigInteger.class, new BigInteger("99"));
        registerCollections();
        registerPrimitives();
    }

    private void registerCollections() {
        registerTestInstanceFor(Set.class, new TreeSet<>());
        registerTestInstanceFor(Collection.class, new ArrayList<>());
        registerTestInstanceFor(List.class, new ArrayList<>());
        registerTestInstanceFor(Iterable.class, new ArrayList<>());
        registerTestInstanceFor(Map.class, new HashMap<>());
    }

    private void registerPrimitives() {
        registerTestInstanceFor(Integer.class, Integer.MAX_VALUE);
        registerTestInstanceFor(int.class, Integer.MAX_VALUE);
        registerTestInstanceFor(Byte.class, Byte.MAX_VALUE);
        registerTestInstanceFor(byte.class, Byte.MAX_VALUE);
        registerTestInstanceFor(Short.class, Short.MAX_VALUE);
        registerTestInstanceFor(short.class, Short.MAX_VALUE);
        registerTestInstanceFor(Long.class, Long.MAX_VALUE);
        registerTestInstanceFor(long.class, Long.MAX_VALUE);
        registerTestInstanceFor(Float.class, Float.MAX_VALUE);
        registerTestInstanceFor(float.class, Float.MAX_VALUE);
        registerTestInstanceFor(Double.class, Double.MAX_VALUE);
        registerTestInstanceFor(double.class, Double.MAX_VALUE);
        registerTestInstanceFor(Boolean.class, Boolean.TRUE);
        registerTestInstanceFor(boolean.class, Boolean.TRUE);
    }

    private Object instatiateClass(Class<?> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create value for class " + type.getName(), e);
        }
    }
}
