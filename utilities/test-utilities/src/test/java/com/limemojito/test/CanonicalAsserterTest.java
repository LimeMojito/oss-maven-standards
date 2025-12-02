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


import org.junit.jupiter.api.Test;

import static com.limemojito.test.CanonicalAsserter.assertCanonical;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CanonicalAsserterTest {

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void shouldAssertCanonicalOk() {
        String instance = "Instance";
        String duplicate = instance;
        String other = "Other";
        assertCanonical(instance, duplicate, other);
    }

    @Test
    public void shouldAssertFailOnEqualOther() {
        assertThatThrownBy(() -> assertCanonical("Instance",
                                                 "Instance",
                                                 "Instance"))
                .isInstanceOf(AssertionError.class);
    }


    @Test
    public void shouldAssertFailOnRawObject() {
        assertCanonical("Instance", "Instance", new Object());
    }


    @Test
    public void shouldHaveDefaultToStringFailure() {
        assertThatThrownBy(() -> assertCanonical(new Object(), new Object(), "SecureTestConfiguration"))
                .isInstanceOf(AssertionError.class);
    }
}
