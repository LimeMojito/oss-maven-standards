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

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.AccessMode;
import java.util.*;

public class AccessorAsserterTest {

    @Test
    public void beanGettersOk() throws Exception {
        AccessorAsserter.assertGetters(createPopulatedBean());
    }

    @Test
    public void beanGetSetOnPopulatedOk() throws Exception {
        AccessorAsserter.assertGettersAndSetters(createPopulatedBean());
    }

    @Test(expected = InvocationTargetException.class)
    public void badGetterStopsAssert() throws Exception {
        class SomeBean {
            public String getBang() {
                throw new RuntimeException("bang");
            }
        }
        AccessorAsserter.assertGetters(new SomeBean());
    }

    @Test
    public void brokenWriteMethodDetected() throws Exception {
        class BrokenWriteBehaviour {
            @SuppressWarnings({"UnusedDeclaration"})
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
            Assert.fail("Should throw exception");
        } catch (AssertionError e) {
            final String message = e.getMessage();
            Assert.assertTrue("Message " + message + " not expected", message.contains("Read method failed"));
        }

    }

    @Test
    public void shouldShowNoNullProperties() throws Exception {
        SimpleModel one = new SimpleModel("key");
        one.setAttribute(33);
        AccessorAsserter.assertNotNullMembers(one);
    }

    @Test(expected = AssertionError.class)
    public void shouldFailOnNull() throws Exception {
        SimpleModel one = new SimpleModel("key");
        AccessorAsserter.assertNotNullMembers(one);
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
                            new TreeSet<Integer>(),
                            new LinkedList<Integer>(),
                            new HashMap<Integer, Integer>());
    }

    static class TestBean {
        private String a;
        private byte c;
        private int b;
        private long d;
        private float e;
        private double f;
        private Object g;
        private Integer boxed;
        @SuppressWarnings({"UnusedDeclaration"})
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

        public List<String> getCollection() {
            return collection;
        }

        public void setCollection(List<String> collection) {
            this.collection = new ArrayList<String>(collection);
        }

        public void setWriteOnly(boolean writeOnly) {
            this.writeOnly = writeOnly;
        }

        public AccessMode getAccessMode() {
            return accessMode;
        }

        public void setAccessMode(AccessMode accessMode) {
            this.accessMode = accessMode;
        }

        public Integer getBoxed() {
            return boxed;
        }

        public void setBoxed(Integer boxed) {
            this.boxed = boxed;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public byte getC() {
            return c;
        }

        public void setC(byte c) {
            this.c = c;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public long getD() {
            return d;
        }

        public void setD(long d) {
            this.d = d;
        }

        public float getE() {
            return e;
        }

        public void setE(float e) {
            this.e = e;
        }

        public double getF() {
            return f;
        }

        public void setF(double f) {
            this.f = f;
        }

        public Object getG() {
            return g;
        }

        public void setG(Object g) {
            this.g = g;
        }

        public short getS() {
            return s;
        }

        public void setS(short s) {
            this.s = s;
        }

        public BigDecimal getBigDecimal() {
            return bigDecimal;
        }

        public BigInteger getBigInteger() {
            return bigInteger;
        }

        public void setBigDecimal(BigDecimal bigDecimal) {
            this.bigDecimal = bigDecimal;
        }

        public void setBigInteger(BigInteger bigInteger) {
            this.bigInteger = bigInteger;
        }

        public Map<Integer, Integer> getaHashMap() {
            return aHashMap;
        }

        public Byte getaByte() {
            return aByte;
        }

        public void setaByte(Byte aByte) {
            this.aByte = aByte;
        }

        public Short getaShort() {
            return aShort;
        }

        public void setaShort(Short aShort) {
            this.aShort = aShort;
        }

        public Long getaLong() {
            return aLong;
        }

        public void setaLong(Long aLong) {
            this.aLong = aLong;
        }

        public Integer getaInteger() {
            return aInteger;
        }

        public void setaInteger(Integer aInteger) {
            this.aInteger = aInteger;
        }

        public Float getaFloat() {
            return aFloat;
        }

        public void setaFloat(Float aFloat) {
            this.aFloat = aFloat;
        }

        public Double getaDouble() {
            return aDouble;
        }

        public void setaDouble(Double aDouble) {
            this.aDouble = aDouble;
        }

        public Boolean getaBoolean() {
            return aBoolean;
        }

        public void setaBoolean(Boolean aBoolean) {
            this.aBoolean = aBoolean;
        }

        public Set<Integer> getaTreeSet() {
            return aTreeSet;
        }

        public void setaTreeSet(TreeSet<Integer> aTreeSet) {
            this.aTreeSet = aTreeSet;
        }

        public void setaHashMap(Map<Integer, Integer> aHashMap) {
            this.aHashMap = aHashMap;
        }

        public void setaTreeSet(Set<Integer> aTreeSet) {
            this.aTreeSet = aTreeSet;
        }
    }
}
