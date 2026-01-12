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

import static com.limemojito.test.AccessorAsserter.assertGettersAndSetters;
import static com.limemojito.test.CanonicalAsserter.assertCanonical;
import static com.limemojito.test.JsonAsserter.assertSerializeDeserialize;

/**
 * The ModelAsserter class is used to assert the behavior of a model object by performing several tests. It provides
 * methods to assert the canonical behavior, the accessibility of
 * getters and setters, and the serialization and deserialization of the model object to and from JSON.
 *
 * <p>The class contains a single static method, {@code assertModelBehaviour}, which takes the model object to test, a duplicate object (expected to have the same ID), a "different
 * " object for comparison, and an optional list of properties to ignore during the getter and setter accessibility test. The method performs the following tests:</p>
 * <ol>
 *     <li>Checks the canonical behavior by asserting equals, hashCode, and toString methods.</li>
 *     <li>Checks the accessibility of all getters and setters of the model object, ignoring the specified properties.</li>
 *     <li>Checks the serialization and deserialization of the model object to and from JSON.</li>
 * </ol>
 */
public class ModelAsserter {
    /**
     * Asserts the behaviour of a model object by performing the following tests:
     * 1. Checks the canonical behaviour by asserting equals, hashCode, and toString methods.
     * 2. Checks the accessibility of all getters and setters of the model object, ignoring the specified properties.
     * 3. Checks the serialization and deserialization of the model object to and from JSON.
     *
     * @param model            The model object to test.
     * @param duplicate        A duplicate of the model object (expected to have the same ID).
     * @param other            A "different" object for comparison.
     * @param ignoreProperties Properties to ignore during the getter and setter accessibility test.
     */
    public static void assertModelBehaviour(Object model,
                                            Object duplicate,
                                            Object other,
                                            String... ignoreProperties) {
        assertCanonical(model, duplicate, other);
        assertGettersAndSetters(model, ignoreProperties);
        assertSerializeDeserialize(model);
    }
}
