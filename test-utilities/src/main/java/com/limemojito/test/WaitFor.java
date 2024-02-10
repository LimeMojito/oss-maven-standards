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

import static org.assertj.core.api.Assertions.assertThat;

public class WaitFor {

    private static final int DEFAULT_POLLING_DELAY = 100;

    public static void waitFor(int maxWaitSeconds, long pollingDelayMs, SituationToBecomeTrue t) throws Exception {
        boolean situation = waitForSituationOrTimeout(maxWaitSeconds, pollingDelayMs, t);
        assertThat(situation).withFailMessage("Situation did not occur in %d seconds", maxWaitSeconds)
                             .isTrue();
    }

    public static void waitFor(int maxWaitSeconds, SituationToBecomeTrue t) throws Exception {
        waitFor(maxWaitSeconds, DEFAULT_POLLING_DELAY, t);
    }

    private static boolean waitForSituationOrTimeout(int maxWaitSeconds,
                                                     long pollingDelayMs,
                                                     SituationToBecomeTrue t) throws Exception {
        final long endTime = System.currentTimeMillis() + (1_000L * maxWaitSeconds);
        boolean situation = t.situation();
        while (!situation && endTime > System.currentTimeMillis()) {
            //noinspection BusyWait
            Thread.sleep(pollingDelayMs);
            situation = t.situation();
        }
        return situation;
    }

    public interface SituationToBecomeTrue {
        boolean situation() throws Exception;
    }
}
