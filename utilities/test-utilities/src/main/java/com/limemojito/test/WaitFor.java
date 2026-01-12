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

import static org.assertj.core.api.Assertions.assertThat;


/**
 * The {@code WaitFor} class provides utility methods for waiting for a specific situation to become true.
 * It allows you to specify a maximum wait time and a polling delay to continuously check the situation until it becomes true.
 * If the situation does not become true within the specified time, an exception is thrown.
 *
 * <p>This class is deprecated and is not recommended for use. Use Awaitility instead.
 * </p>
 *
 * <p>To use the {@code WaitFor} class, you need to implement the {@link SituationToBecomeTrue} interface and provide
 * a custom implementation for the {@code situation} method, which defines the condition that needs to be met.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 *     WaitFor.waitFor(10, () -> {
 *         // Check if the situation is true
 *         // Return true if the situation is met, false otherwise
 *     });
 * }</pre>
 *
 * @see org.awaitility.Awaitility
 * @deprecated This class is deprecated and is not recommended for use.
 */
@Deprecated
public class WaitFor {

    private static final int DEFAULT_POLLING_DELAY = 100;

    /**
     * Waits for a specific situation to become true.
     *
     * <p>This method allows you to specify a maximum wait time and a polling delay to continuously check the situation until it becomes true.
     * If the situation does not become true within the specified time, an exception is thrown.</p>
     *
     * <p>Parameters:</p>
     * <ul>
     *     <li>{@code maxWaitSeconds} - the maximum time to wait in seconds</li>
     *     <li>{@code pollingDelayMs} - the delay between polling the situation in milliseconds</li>
     *     <li>{@code t} - the implementation of the {@link SituationToBecomeTrue} interface that defines the condition that needs to be met</li>
     * </ul>
     *
     * <p>Throws:</p>
     * <ul>
     *     <li>{@link Exception} - if the situation does not become true within the specified time</li>
     * </ul>
     *
     * <p>Usage example:</p>
     * <pre>{@code
     *     waitFor(10, 1000, () -> {
     *         // Check if the situation is true
     *         // Return true if the situation is met, false otherwise
     *     });
     * }</pre>
     *
     * @param maxWaitSeconds the maximum time to wait in seconds
     * @param pollingDelayMs the delay between polling the situation in milliseconds
     * @param t              the implementation of the {@link SituationToBecomeTrue} interface that defines the condition that needs to be met
     * @throws Exception if the situation does not become true within the specified time
     * @see org.awaitility.Awaitility
     * @deprecated This method is deprecated and is not recommended for use. Use Awaitility instead.
     */
    @Deprecated
    public static void waitFor(int maxWaitSeconds, long pollingDelayMs, SituationToBecomeTrue t) throws Exception {
        boolean situation = waitForSituationOrTimeout(maxWaitSeconds, pollingDelayMs, t);
        assertThat(situation).withFailMessage("Situation did not occur in %d seconds", maxWaitSeconds)
                             .isTrue();
    }

    /**
     * Waits for a specific situation to become true using the default polling delay.
     *
     * <p>This method allows you to specify a maximum wait time and a polling delay to continuously check the situation until it becomes true.
     * If the situation does not become true within the specified time, an exception is thrown.</p>
     *
     * <p>Parameters:</p>
     * <ul>
     *     <li>{@code maxWaitSeconds} - the maximum time to wait in seconds</li>
     *     <li>{@code pollingDelayMs} - the delay between polling the situation in milliseconds</li>
     *     <li>{@code t} - the implementation of the {@link SituationToBecomeTrue} interface that defines the condition that needs to be met</li>
     * </ul>
     *
     * <p>Throws:</p>
     * <ul>
     *     <li>{@link Exception} - if the situation does not become true within the specified time</li>
     * </ul>
     *
     * <p>Usage example:</p>
     * <pre>{@code
     *     waitFor(10, 1000, () -> {
     *         // Check if the situation is true
     *         // Return true if the situation is met, false otherwise
     *     });
     * }</pre>
     *
     * @param maxWaitSeconds the maximum time to wait in seconds
     * @param t              the implementation of the {@link SituationToBecomeTrue} interface that defines the condition that needs to be met
     * @throws Exception if the situation does not become true within the specified time
     * @see org.awaitility.Awaitility
     * @see #DEFAULT_POLLING_DELAY
     * @deprecated This method is deprecated and is not recommended for use. Use Awaitility instead.
     */
    @Deprecated
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

    /**
     * The SituationToBecomeTrue interface defines a method {@code situation()} that needs to be implemented by classes
     * that want to specify a condition that needs to be met for a specific situation to become true.
     *
     * @see WaitFor
     * @deprecated This class is deprecated and is not recommended for use. Use Awaitility instead.
     */
    @Deprecated
    public interface SituationToBecomeTrue {
        /**
         * Test for the given situation and return true or false if it exists.  An exception thrown is considered false.
         *
         * @return true if the situation has occurred.
         * @throws Exception on an error, situation is considered false.
         */
        boolean situation() throws Exception;
    }
}
