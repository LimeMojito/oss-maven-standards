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

import com.limemojito.json.JsonMapperPrototype;
import lombok.RequiredArgsConstructor;

/**
 * A utility class for asserting that a Data Transfer Object (DTO) can be serialized to and deserialized from JSON.
 */
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class DtoAsserter {

    private static final DtoAsserter INSTANCE = new DtoAsserter(new JsonAsserter(JsonMapperPrototype.buildBootLikeMapper()));
    private final JsonAsserter jsonAsserter;

    /**
     * Assert that a Data Transfer Object can be serialized to and from json.
     *
     * @param dto object to check.
     */
    public static void assertDto(Object dto) {
        INSTANCE.assertValid(dto);
    }

    /**
     * Assert that a Data Transfer Object can be serialized to and from json.
     *
     * @param dto object to check.
     */
    public void assertValid(Object dto) {
        AccessorAsserter.assertGetters(dto);
        jsonAsserter.assertSerializeAndDeserialize(dto);
    }
}
