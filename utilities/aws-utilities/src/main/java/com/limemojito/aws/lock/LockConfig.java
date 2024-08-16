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

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClientOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.concurrent.TimeUnit;

/**
 * Set up a DynamoDB table (com.limemojito.aws.lock.tableName) that has a hash key on a key with the name key.
 */
@Configuration
@Slf4j
public class LockConfig {

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

    @Bean
    public DynamoDbLockService dynamoDbLockService(AmazonDynamoDBLockClient client) {
        return new DynamoDbLockService(client);
    }
}
