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

import org.junit.Test;

public class ModelAsserterTest {

    @Test
    public void shouldBeModel() throws Exception {
        SimpleModel one = new SimpleModel("key");
        SimpleModel duplicate = new SimpleModel("key");
        SimpleModel other = new SimpleModel("other");
        ModelAsserter.assertModelBehaviour(one, duplicate, other);
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void shouldHaveCoverage() {
        new ModelAsserter();
    }
}
