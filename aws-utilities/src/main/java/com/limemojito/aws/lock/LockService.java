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

package com.limemojito.aws.lock;

import java.util.Optional;

public interface LockService {

    /**
     * Try to acquire a lock, return a lock resource if the acquire operation succeeds.
     * *
     *
     * @param lockName Name of lock to take (case-sensitive).
     * @return Optional distributed lock resource.
     */
    Optional<DistributedLock> tryAcquire(String lockName);

    /**
     * Block and wait for the named lock to be acquired.
     *
     * @param lockName Name of lock to take (case-sensitive).
     * @return Lock resource.
     */
    DistributedLock acquire(String lockName);

    /**
     * Lock Resource.  Designed for use in a try-with-resources block.
     */
    interface DistributedLock extends AutoCloseable {
        String getName();

        @Override
        void close();
    }
}
