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

package com.limemojito.aws.lock;

import com.amazonaws.services.dynamodbv2.AcquireLockOptions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClient;
import com.amazonaws.services.dynamodbv2.LockItem;
import com.limemojito.lock.LockService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The DynamoDbLockService class is an implementation of the LockService interface that provides methods for acquiring and managing distributed locks using Amazon DynamoDB as the
 * underlying storage mechanism.
 * <p>
 * This class utilizes the AmazonDynamoDBLockClient class from the AWS SDK for Java to interact with the DynamoDB table that stores the locks.
 * <p>
 * Note: The DynamoDbLockService class assumes the existence of a DynamoDB table with the necessary schema for storing locks.
 * You should ensure that the table is set up and configured correctly before using this class.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DynamoDbLockService implements LockService {

    private final AmazonDynamoDBLockClient client;

    @Override
    @SneakyThrows
    public synchronized Optional<DistributedLock> tryAcquire(String lockName) {
        return client.tryAcquireLock(AcquireLockOptions.builder(lockName).build())
                     .map(item -> new DynamoDbLock(item, lockName));
    }

    @Override
    @SneakyThrows
    public DistributedLock acquire(String lockName) {
        final LockItem lockItem = client.acquireLock(AcquireLockOptions.builder(lockName).build());
        return new DynamoDbLock(lockItem, lockName);
    }

    protected static class DynamoDbLock implements LockService.DistributedLock {
        private final LockItem lockItem;
        @Getter
        private final String name;

        public DynamoDbLock(LockItem lockItem, String name) {
            this.lockItem = lockItem;
            this.name = name;
            log.info("Acquired DynamoDb lock for {}", name);
        }

        @Override
        public void close() {
            lockItem.close();
        }
    }
}
