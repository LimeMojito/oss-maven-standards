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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.assertj.core.api.Assertions.assertThat;


public class SerializableAsserter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SerializableAsserter.class);
    private static final int BUFFER_SIZE = 8096;

    public static void assertSerializable(Object o) throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        LOGGER.debug("Attempting Serialization");
        try (ObjectOutputStream outputStream = new ObjectOutputStream(out)) {
            outputStream.writeObject(o);
        }

        byte[] data = out.toByteArray();
        LOGGER.info("Serialized to {} bytes", data.length);

        LOGGER.debug("Attempting Deserialization");
        try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object found = inputStream.readObject();
            assertThat(found).isEqualTo(o);
        }
        LOGGER.debug("is Serializable");
    }
}
