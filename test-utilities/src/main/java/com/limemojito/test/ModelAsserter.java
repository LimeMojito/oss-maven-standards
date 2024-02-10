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

import static com.limemojito.test.AccessorAsserter.assertGettersAndSetters;
import static com.limemojito.test.CanonicalAsserter.assertCanonical;
import static com.limemojito.test.JsonAsserter.assertSerializeDeserialize;

public class ModelAsserter {
    public static void assertModelBehaviour(Object model,
                                            Object duplicate,
                                            Object other,
                                            String... ignoreProperties) throws Exception {
        assertCanonical(model, duplicate, other);
        assertGettersAndSetters(model, ignoreProperties);
        assertSerializeDeserialize(model);
    }
}
