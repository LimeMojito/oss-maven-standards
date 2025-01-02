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

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClientOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.concurrent.TimeUnit;

/**
 * Configures a DynamoDb implementation of the LockService.  Will create the dynamodb table if required.
 *
 * @see com.limemojito.lock.LockService
 */
@Configuration
@Slf4j
public class DynamodbLockServiceConfig {

    /**
     * Amazon DynamoDbLockClient implementation to wrap.  May create the lock database if it is not present.
     *
     * @param dynamoDB        The DynamoDB client used to interact with the lock table.
     * @param tableName       The name of the lock table.
     * @param leaseDuration   The duration of the lease for the locks, in seconds.
     * @param heartbeatPeriod The period at which heartbeat signals are sent, in seconds.
     * @return initialized lock client.
     */
    @Bean(destroyMethod = "close")
    public AmazonDynamoDBLockClient dbLockClient(DynamoDbClient dynamoDB,
                                                 @Value("${com.limemojito.aws.lock.tableName}") String tableName,
                                                 @Value("${com.limemojito.aws.lock.leaseDuration:20}") long leaseDuration,
                                                 @Value("${com.limemojito.aws.lock.heartbeatPeriod:5}") long heartbeatPeriod) {
        log.info("Creating lock client on table {} with lease duration {}s and heartbeat {}s",
                 tableName,
                 leaseDuration,
                 heartbeatPeriod);
        final AmazonDynamoDBLockClientOptions options = AmazonDynamoDBLockClientOptions.builder(dynamoDB,
                                                                                                tableName)
                                                                                       .withTimeUnit(TimeUnit.SECONDS)
                                                                                       .withLeaseDuration(leaseDuration)
                                                                                       .withHeartbeatPeriod(
                                                                                               heartbeatPeriod)
                                                                                       .withCreateHeartbeatBackgroundThread(
                                                                                               true)
                                                                                       .build();
        final AmazonDynamoDBLockClient client = new AmazonDynamoDBLockClient(options);
        client.assertLockTableExists();
        return client;
    }

    /**
     * {@code DynamoDbLockService} is a service class that provides methods for acquiring and managing distributed locks using Amazon DynamoDB as the underlying storage mechanism
     *
     * @param client Amazon Dynamodb Lock Client to delegate to.
     * @return a lock service implementation.
     * @see com.limemojito.lock.LockService
     */
    @Bean
    public DynamoDbLockService dynamoDbLockService(AmazonDynamoDBLockClient client) {
        return new DynamoDbLockService(client);
    }
}
