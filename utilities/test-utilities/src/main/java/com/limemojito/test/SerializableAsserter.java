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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Assert that something can be serialized to and from a byte stream.
 */
@Slf4j
public class SerializableAsserter {
    private static final int BUFFER_SIZE = 8096;

    /**
     * Assert that something can be serialized to and from a byte stream.
     *
     * @param o Instance to be tested.
     */
    @SneakyThrows
    public static void assertSerializable(Object o) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        log.debug("Attempting Serialization");
        try (ObjectOutputStream outputStream = new ObjectOutputStream(out)) {
            outputStream.writeObject(o);
        }

        byte[] data = out.toByteArray();
        log.info("Serialized to {} bytes", data.length);

        log.debug("Attempting Deserialization");
        try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object found = inputStream.readObject();
            assertThat(found).isEqualTo(o);
        }
        log.debug("is Serializable");
    }
}
