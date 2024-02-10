/*
 * Copyright  2011-2024 Lime Mojito Pty Ltd
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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.lang.String.format;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Service
@Slf4j
public class PostgresLockService implements LockService {

    private final JdbcTemplate template;

    public PostgresLockService(JdbcTemplate template) {
        this.template = template;
    }

    /**
     * Lock with pg_try_advisory_xact_lock expecting to be in a transaction.   Unlock is with transaction commit or rollback.
     *
     * @param lockName Name of lock to take (case-sensitive).
     * @return Optional lock resource if the lock was successful.
     * @throws DataAccessException on a db failure.
     * @see PostgresDistributedLock
     */
    @Override
    @Transactional(propagation = MANDATORY)
    public synchronized Optional<DistributedLock> tryAcquire(String lockName) {
        final int lockNumber = name2Int(lockName);
        // If acquired this lock is released on transaction commit.
        final Boolean locked = template.queryForObject(format("SELECT pg_try_advisory_xact_lock(%d)", lockNumber),
                                                       Boolean.class);
        if (locked != null && locked) {
            return Optional.of(new PostgresDistributedLock(lockName));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Lock with pg_advisory_xact_lock expecting to be in a transaction.  This will block for the database's lock timeout and throw a spring
     * jdbc failure on timeout.  Unlock is with transaction commit or rollback.
     *
     * @param lockName Name of lock to take (case-sensitive).
     * @return Lock resource if the lock was successful.
     * @throws DataAccessException on a lock timeout or db failure.
     * @see PostgresDistributedLock
     */
    @Override
    @Transactional(propagation = MANDATORY)
    public synchronized DistributedLock acquire(String lockName) {
        final int lockNumber = name2Int(lockName);
        template.execute(format("SELECT pg_advisory_xact_lock(%d)", lockNumber));
        return new PostgresDistributedLock(lockName);
    }

    /**
     * Unlock is with transaction commit or rollback.
     */
    protected static class PostgresDistributedLock implements DistributedLock {
        @Getter
        private final String name;

        protected PostgresDistributedLock(String lockName) {
            this.name = lockName;
            log.info("Acquired DB lock for {} -> {}", lockName, name2Int(lockName));
        }

        /**
         * Unlock is with transaction commit or rollback.
         */
        @Override
        public void close() {
            // this lock is released on transaction commit or rollback.
            log.info("Releasing lock {}.  DB lock will be released on commit or rollback of Tx.", name);
        }
    }

    private static int name2Int(String lockName) {
        return lockName.hashCode();
    }
}
