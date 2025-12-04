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


import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.AccessMode;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;

public class AccessorAsserterTest {

    @Test
    public void beanGettersOk() {
        AccessorAsserter.assertGetters(createPopulatedBean());
    }

    @Test
    public void beanGetSetOnPopulatedOk() {
        AccessorAsserter.assertGettersAndSetters(createPopulatedBean());
    }

    @Test
    public void badGetterStopsAssert() {
        assertThatThrownBy(() ->
                           {
                               class SomeBean {
                                   @SuppressWarnings("unused")
                                   public String getBang() {
                                       throw new RuntimeException("bang");
                                   }
                               }
                               AccessorAsserter.assertGetters(new SomeBean());
                           }).isInstanceOf(InvocationTargetException.class);
    }

    @Test
    public void brokenWriteMethodDetected() {
        @SuppressWarnings({"LombokSetterMayBeUsed", "unused"})
        class BrokenWriteBehaviour {
            @SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
            private boolean write;

            public boolean isWrite() {
                return false;
            }

            public void setWrite(boolean write) {
                this.write = write;
            }
        }
        try {
            AccessorAsserter.assertGettersAndSetters(new BrokenWriteBehaviour());
            //noinspection ResultOfMethodCallIgnored
            fail("Should throw exception");
        } catch (AssertionError e) {
            final String message = e.getMessage();
            assertThat(message).contains("Read method failed");
        }

    }

    @Test
    public void shouldShowNoNullProperties() {
        SimpleModel one = new SimpleModel("key");
        one.setAttribute(33);
        AccessorAsserter.assertNotNullMembers(one);
    }

    @Test
    public void shouldFailOnNull() {
        assertThatThrownBy(() -> {
            SimpleModel one = new SimpleModel("key");
            AccessorAsserter.assertNotNullMembers(one);
        }).isInstanceOf(AssertionError.class);
    }

    private TestBean createPopulatedBean() {
        return new TestBean("A",
                            (byte) 1,
                            1,
                            1L,
                            1.0f,
                            1.0,
                            new Object(),
                            (short) 1,
                            new BigDecimal("10.1"),
                            new BigInteger("10"),
                            (byte) 1,
                            (short) 1,
                            (long) 1,
                            1,
                            (float) 1.0,
                            1.0,
                            true,
                            new TreeSet<>(),
                            new LinkedList<>(),
                            new HashMap<>());
    }

    @Setter
    @Getter
    static class TestBean {
        private String a;
        private byte c;
        private int b;
        private long d;
        private float e;
        private double f;
        private Object g;
        private Integer boxed;
        @SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
        private boolean writeOnly;
        private List<String> collection;
        private Map<Integer, Integer> aHashMap;
        private short s;
        private BigDecimal bigDecimal;
        private BigInteger bigInteger;
        private Byte aByte;
        private Short aShort;
        private Long aLong;
        private Integer aInteger;
        private Float aFloat;
        private Double aDouble;
        private Boolean aBoolean;
        private Set<Integer> aTreeSet;
        private AccessMode accessMode;

        TestBean(String a,
                 byte c,
                 int b,
                 long d,
                 float e,
                 double f,
                 Object g,
                 short s,
                 BigDecimal bigDecimal,
                 BigInteger bigInteger,
                 Byte aByte,
                 Short aShort,
                 Long aLong,
                 Integer aInteger,
                 Float aFloat,
                 Double aDouble,
                 Boolean aBoolean,
                 TreeSet<Integer> aTreeSet,
                 Collection<Integer> aCollection,
                 HashMap<Integer, Integer> aHashMap) {
            this.a = a;
            this.c = c;
            this.b = b;
            this.d = d;
            this.e = e;
            this.f = f;
            this.g = g;
            this.boxed = b;
            this.s = s;
            this.bigDecimal = bigDecimal;
            this.bigInteger = bigInteger;
            this.aByte = aByte;
            this.aShort = aShort;
            this.aLong = aLong;
            this.aInteger = aInteger;
            this.aFloat = aFloat;
            this.aDouble = aDouble;
            this.aBoolean = aBoolean;
            this.aTreeSet = aTreeSet;
            this.aHashMap = aHashMap;
            this.collection = new LinkedList<>();
            this.accessMode = AccessMode.EXECUTE;
        }
    }
}
