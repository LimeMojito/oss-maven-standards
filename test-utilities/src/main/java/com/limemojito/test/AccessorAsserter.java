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

package com.limemojito.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Asserts the accessors (Getters and Setters) of a class.
 */
public final class AccessorAsserter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessorAsserter.class);
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
     * @throws Exception if the getters fail.
     */
    public static void assertGetters(Object o, String... ignoreProperties) throws Exception {
        INSTANCE.assertGets(o, ignoreProperties);
    }

    /**
     * Check that all read and write accessors may be called.
     *
     * @param o                object to test
     * @param ignoreProperties Properties to ignore for tests
     * @throws Exception if the accessors fail.
     */
    public static void assertGettersAndSetters(Object o, String... ignoreProperties) throws Exception {
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
    public static void assertNotNullMembers(Object object, String... ignoreMembers) throws Exception {
        INSTANCE.assertNotNullProperties(object, ignoreMembers);
    }

    public void assertNotNullProperties(Object object, String... ignoreMembers) throws Exception {
        visitProperties(object, new IgnorePropertyVisitor(ignoreMembers) {
            @Override
            public void visitDescriptor(Object instance, PropertyDescriptor d) throws Exception {
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
     * @throws Exception if the getters fail.
     */
    public void assertGets(Object o, String... ignoreProperties) throws Exception {
        visitProperties(o, new IgnorePropertyVisitor(ignoreProperties) {
            @Override
            public void visitDescriptor(Object instance, PropertyDescriptor d) throws Exception {
                Method read = d.getReadMethod();
                if (read != null) {
                    callGetter(instance, read);
                }
            }
        });
    }

    /**
     * Check that all read and write accessors may be called.
     *
     * @param o                object to test
     * @param ignoreProperties Properties to ignore for tests
     * @throws Exception if the accessors fail.
     */
    public void assertGetsAndSets(Object o, String... ignoreProperties) throws Exception {
        visitProperties(o, new IgnorePropertyVisitor(ignoreProperties) {
            @Override
            public void visitDescriptor(Object o, PropertyDescriptor descriptor) throws Exception {
                final Method readMethod = descriptor.getReadMethod();
                final Method writeMethod = descriptor.getWriteMethod();
                if (readMethod != null) {
                    callGetter(o, readMethod);
                }
                if (writeMethod != null) {
                    Class<?> type = writeMethod.getParameterTypes()[0];
                    final Object testValue = testValueFactory.createFor(type);
                    Object wrote = callSetter(writeMethod, o, testValue);
                    if (readMethod != null) {
                        checkWriteValue(o, readMethod, wrote);
                    }
                }
            }

            private void checkWriteValue(Object o, Method readMethod, Object wrote) throws Exception {
                final Object value = readMethod.invoke(o);
                assertNotNull("Read is null after write " + readMethod, value);
                assertEquals("Read method failed on " + readMethod, wrote, value);
            }
        });
    }

    private interface VisitDescriptor {
        void visit(Object instance, PropertyDescriptor d) throws Exception;
    }

    private abstract class IgnorePropertyVisitor implements VisitDescriptor {
        private final List<String> ignorePropertiesList;

        IgnorePropertyVisitor(String[] ignoreProperties) {
            ignorePropertiesList = new ArrayList<>(DEFAULT_IGNORES);
            ignorePropertiesList.addAll(asList(ignoreProperties));
        }

        @Override
        public void visit(Object o, PropertyDescriptor descriptor) throws Exception {
            if (!ignorePropertiesList.contains(descriptor.getName())) {
                visitDescriptor(o, descriptor);
            }
        }

        protected abstract void visitDescriptor(Object o, PropertyDescriptor descriptor) throws Exception;
    }

    private void visitProperties(Object o, VisitDescriptor visitor) throws Exception {
        final Class<?> clazz = o.getClass();
        final BeanInfo info = Introspector.getBeanInfo(clazz);
        final PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        for (final PropertyDescriptor descriptor : descriptors) {
            visitor.visit(o, descriptor);
        }
    }

    private static Object callGetter(Object o, Method readMethod) throws Exception {
        LOGGER.debug("Invoking " + readMethod);
        return readMethod.invoke(o);
    }

    private static Object callSetter(Method writeMethod, Object instance, Object testValue)
            throws IllegalAccessException, InvocationTargetException {
        LOGGER.debug("Invoking " + writeMethod);
        writeMethod.invoke(instance, testValue);
        return testValue;
    }
}
