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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Asserts the accessors (Getters and Setters) of a class.
 */
@Slf4j
public final class AccessorAsserter {
    private static final AccessorAsserter INSTANCE = new AccessorAsserter();
    private static final List<String> DEFAULT_IGNORES = List.of("class");
    private final TestValueFactory testValueFactory;

    private AccessorAsserter() {
        testValueFactory = new TestValueFactory();
    }

    /**
     * Check that all read accessors may be called.
     *
     * @param o                object to test
     * @param ignoreProperties Properties to ignore for tests
     */
    public static void assertGetters(Object o, String... ignoreProperties) {
        INSTANCE.assertGets(o, ignoreProperties);
    }

    /**
     * Check that all read and write accessors may be called.
     *
     * @param o                object to test
     * @param ignoreProperties Properties to ignore for tests
     */
    public static void assertGettersAndSetters(Object o, String... ignoreProperties) {
        INSTANCE.assertGetsAndSets(o, ignoreProperties);
    }

    /**
     * Register an instance to intercept for an unknown class for getters and setters.  Useful to map an
     * abstract reference to a concrete type.
     *
     * @param clazz    Class that will be requested.
     * @param instance An instance (may be subclass) to return.
     */
    public static void registerTestInstanceFor(Class<?> clazz, Object instance) {
        INSTANCE.testValueFactory.registerTestInstanceFor(clazz, instance);
    }

    /**
     * Assert that all members are not null.
     *
     * @param object        Object to check.
     * @param ignoreMembers members to ignore by property name.
     */
    public static void assertNotNullMembers(Object object, String... ignoreMembers) {
        INSTANCE.assertNotNullProperties(object, ignoreMembers);
    }

    /**
     * Assert that all members are not null.
     *
     * @param object        Object to check.
     * @param ignoreMembers members to ignore by property name.
     */
    public void assertNotNullProperties(Object object, String... ignoreMembers) {
        visitProperties(object, new IgnorePropertyVisitor(ignoreMembers) {
            @Override
            @SneakyThrows
            public void visitDescriptor(Object instance, PropertyDescriptor d) {
                final Method readMethod = d.getReadMethod();
                if (readMethod != null) {
                    assertNotNull(readMethod.invoke(instance));
                }
            }
        });
    }

    /**
     * Check that all read accessors may be called.
     *
     * @param o                object to test
     * @param ignoreProperties Properties to ignore for tests
     */
    public void assertGets(Object o, String... ignoreProperties) {
        visitProperties(o, new IgnorePropertyVisitor(ignoreProperties) {
            @Override
            @SneakyThrows
            public void visitDescriptor(Object instance, PropertyDescriptor d) {
                Method read = d.getReadMethod();
                if (read != null) {
                    final Object value = callGetter(instance, read);
                }
            }
        });
    }

    /**
     * Check that all read and write accessors may be called.
     *
     * @param o                object to test
     * @param ignoreProperties Properties to ignore for tests
     */
    public void assertGetsAndSets(Object o, String... ignoreProperties) {
        visitProperties(o, new IgnorePropertyVisitor(ignoreProperties) {
            @Override
            public void visitDescriptor(Object o, PropertyDescriptor descriptor) {
                final Method readMethod = descriptor.getReadMethod();
                final Method writeMethod = descriptor.getWriteMethod();
                if (readMethod != null) {
                    callGetter(o, readMethod);
                }
                if (writeMethod != null) {
                    Class<?> type = writeMethod.getParameterTypes()[0];
                    final Object testValue = testValueFactory.createFor(type);
                    Object wrote = callSetter(o, writeMethod, testValue);
                    if (readMethod != null) {
                        checkWriteValue(o, readMethod, wrote);
                    }
                }
            }

            @SneakyThrows
            private void checkWriteValue(Object o, Method readMethod, Object wrote) {
                final Object value = callGetter(o, readMethod);
                assertNotNull("Read is null after write " + readMethod, value);
                assertEquals("Read method failed on " + readMethod, wrote, value);
            }
        });
    }

    private interface VisitDescriptor {
        void visit(Object instance, PropertyDescriptor d);
    }

    private abstract static class IgnorePropertyVisitor implements VisitDescriptor {
        private final List<String> ignorePropertiesList;

        IgnorePropertyVisitor(String[] ignoreProperties) {
            ignorePropertiesList = new ArrayList<>(DEFAULT_IGNORES);
            ignorePropertiesList.addAll(asList(ignoreProperties));
        }

        @Override
        public void visit(Object o, PropertyDescriptor descriptor) {
            if (!ignorePropertiesList.contains(descriptor.getName())) {
                visitDescriptor(o, descriptor);
            }
        }

        protected abstract void visitDescriptor(Object o, PropertyDescriptor descriptor);
    }

    @SneakyThrows
    private void visitProperties(Object o, VisitDescriptor visitor) {
        final Class<?> clazz = o.getClass();
        final BeanInfo info = Introspector.getBeanInfo(clazz);
        final PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        for (final PropertyDescriptor descriptor : descriptors) {
            visitor.visit(o, descriptor);
        }
    }

    @SneakyThrows
    private static Object callGetter(Object o, Method readMethod) {
        log.debug("Invoking {}", readMethod);
        return readMethod.invoke(o);
    }

    @SneakyThrows
    private static Object callSetter(Object instance, Method writeMethod, Object testValue) {
        log.debug("Invoking {} with {}", writeMethod, testValue);
        writeMethod.invoke(instance, testValue);
        return testValue;
    }
}
