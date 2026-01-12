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

package com.limemojito.aws.lock;

import com.amazonaws.services.dynamodbv2.AcquireLockOptions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClient;
import com.amazonaws.services.dynamodbv2.LockItem;
import com.limemojito.lock.LockService.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class DynamoDbLockServiceTest {

    @Mock
    private AmazonDynamoDBLockClient lockClient;
    @Mock
    private LockItem lockItem;
    @InjectMocks
    private DynamoDbLockService lockService;
    @Captor
    private ArgumentCaptor<AcquireLockOptions> optionsCaptor;

    @AfterEach
    public void verifyMocks() {
        verifyNoMoreInteractions(lockClient, lockItem);
    }

    @Test
    public void shouldTryToAcquireLock() throws InterruptedException {
        final String lockName = "my-lock";
        doReturn(Optional.of(lockItem)).when(lockClient).tryAcquireLock(optionsCaptor.capture());

        final Optional<DistributedLock> lock = lockService.tryAcquire(lockName);
        if (lock.isPresent()) {
            try (final DistributedLock distributedLock = lock.get()) {
                log.info("Hello I am in exclusive lock {}", distributedLock.getName());
            }
        }

        assertThat(lock).isPresent();
        verify(lockClient).tryAcquireLock(optionsCaptor.getValue());
        verify(lockItem).close();
    }

    @Test
    public void shouldFailTryToAcquireLock() throws InterruptedException {
        final String lockName = "my-lock";
        doReturn(Optional.empty()).when(lockClient).tryAcquireLock(optionsCaptor.capture());

        final Optional<DistributedLock> lock = lockService.tryAcquire(lockName);

        assertThat(lock).isEmpty();
        verify(lockClient).tryAcquireLock(optionsCaptor.getValue());
    }

    @Test
    public void shouldPerformAcquire() throws InterruptedException {
        final String lockName = "my-lock";
        doReturn(lockItem).when(lockClient).acquireLock(optionsCaptor.capture());

        try (DistributedLock lock = lockService.acquire(lockName)) {
            log.info("Hello I am in exclusive lock {}", lock.getName());
            assertThat(lock.getName()).isEqualTo(lockName);
        }
        verify(lockClient).acquireLock(optionsCaptor.getValue());
        verify(lockItem).close();
    }
}
