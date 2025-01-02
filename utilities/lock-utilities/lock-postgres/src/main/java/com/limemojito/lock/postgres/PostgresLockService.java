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

package com.limemojito.lock.postgres;

import com.limemojito.lock.LockService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.lang.String.format;

/**
 * The {@code PostgresLockService} class is an implementation of the {@link LockService} interface that provides methods for acquiring and managing distributed locks using PostgreSQL
 * database.
 * <p>
 * The class utilizes the Postgres advisory lock functions to acquire and release locks. The locks are acquired within a transaction and are automatically released on transaction
 * commit or rollback.
 * </p>
 *
 * <p>
 * To use {@code PostgresLockService}, an instance of {@link JdbcTemplate} is required to be passed in the constructor. The {@link JdbcTemplate} provides the necessary database access
 * for acquiring and releasing locks.
 * </p>
 *
 * <p>
 * This class is thread-safe, and the methods for acquiring locks are synchronized to ensure proper concurrency control.
 * </p>
 *
 * @see LockService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostgresLockService implements LockService {

    private final JdbcTemplate template;

    /**
     * Lock with pg_try_advisory_xact_lock expecting to be in a transaction.   Unlock is with transaction commit or rollback.
     *
     * @param lockName Name of lock to take (case-sensitive).
     * @return Optional lock resource if the lock was successful.
     * @throws DataAccessException on a db failure.
     * @see PostgresDistributedLock
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
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
    @Transactional(propagation = Propagation.MANDATORY)
    public synchronized DistributedLock acquire(String lockName) {
        final int lockNumber = name2Int(lockName);
        template.execute(format("SELECT pg_advisory_xact_lock(%d)", lockNumber));
        return new PostgresDistributedLock(lockName);
    }

    /**
     * Unlock is with transaction commit or rollback.
     */
    @Getter
    protected static class PostgresDistributedLock implements DistributedLock {
        private final String name;

        /**
         * Represents a distributed lock in the PostgreSQL database.
         * The lock is acquired when an instance of PostgresDistributedLock is created and released when the associated transaction is committed or rolled back.
         *
         * @param lockName the name of the lock
         */
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
