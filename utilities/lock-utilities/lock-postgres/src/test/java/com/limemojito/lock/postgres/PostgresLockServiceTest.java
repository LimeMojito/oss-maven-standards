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

package com.limemojito.lock.postgres;

import com.limemojito.lock.LockService.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class PostgresLockServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private PostgresLockService lockService;

    @Test
    public void shouldTryToAcquireLock() {
        doReturn(true).when(jdbcTemplate).queryForObject("SELECT pg_try_advisory_xact_lock(1462666412)", Boolean.class);

        final Optional<DistributedLock> lock = lockService.tryAcquire("my-lock");
        if (lock.isPresent()) {
            try (final DistributedLock distributedLock = lock.get()) {
                log.info("Hello I am in exclusive lock {}", distributedLock.getName());
            }
        }

        assertThat(lock).isPresent();
        verify(jdbcTemplate).queryForObject("SELECT pg_try_advisory_xact_lock(1462666412)", Boolean.class);
        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    public void shouldFailTryToAcquireLock() {
        doReturn(false).when(jdbcTemplate)
                       .queryForObject("SELECT pg_try_advisory_xact_lock(1462666412)", Boolean.class);

        final Optional<DistributedLock> lock = lockService.tryAcquire("my-lock");

        assertThat(lock).isEmpty();
        verify(jdbcTemplate).queryForObject("SELECT pg_try_advisory_xact_lock(1462666412)", Boolean.class);
        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    public void shouldPerformAcquire() {
        try (DistributedLock lock = lockService.acquire("my-lock")) {
            log.info("Hello I am in exclusive lock {}", lock.getName());
            assertThat(lock.getName()).isEqualTo("my-lock");
        }
        verify(jdbcTemplate).execute("SELECT pg_advisory_xact_lock(1462666412)");
        verifyNoMoreInteractions(jdbcTemplate);
    }
}
