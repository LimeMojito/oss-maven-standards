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

package com.limemojito.test.dynamodb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.List;

/**
 * DynamoDbSupport is a utility class that provides simplified access to DynamoDB operations using the DynamoDbEnhancedClient.
 * It can be used to delete all items from a DynamoDB table and save a list of objects to a DynamoDB table.
 * <p>
 * To use DynamoDbSupport, it needs to be instantiated as a Spring bean and injected with a DynamoDbEnhancedClient.
 * It can then be used to perform deleteAll and saveAll operations on DynamoDB tables.
 * <p>
 * Example usage:
 * ```
 * DynamoDbSupport support = new DynamoDbSupport(dynamoDbEnhancedClient);
 * support.deleteAll("myTable", MyObject.class);
 * support.saveAll("myTable", objectList, MyObject.class);
 * ```
 *
 * @see <a href="https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/examples-dynamodb-enhanced.html">DynamoDB Enhanced Client Documentation</a>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DynamoDbSupport {
    private final DynamoDbEnhancedClient dbMapper;

    /**
     * Deletes all items from a DynamoDB table.
     *
     * @param tableName the name of the DynamoDB table from which to delete items
     * @param type      the class representing the type of items stored in the table
     * @param <T>       the type of items stored in the table
     */
    public <T> void deleteAll(String tableName, Class<T> type) {
        log.info("Deleting all {}", type.getSimpleName());
        DynamoDbTable<T> table = tableFor(tableName, type);
        int count = 0;
        for (Page<T> t : table.scan()) {
            List<T> items = t.items();
            items.forEach(table::deleteItem);
            count += items.size();
        }
        log.debug("Delete complete. {} items", count);
    }

    /**
     * Saves a list of objects to a DynamoDB table.
     *
     * @param tableName  the name of the DynamoDB table to save the objects to
     * @param objectList the list of objects to save
     * @param type       the class representing the type of objects in the list
     * @param <T>        the type of objects in the list
     */
    public <T> void saveAll(String tableName, List<T> objectList, Class<T> type) {
        if (!objectList.isEmpty()) {
            log.info("Saving {} objects", objectList.size());
            DynamoDbTable<T> dynamoDbTable = tableFor(tableName, type);
            objectList.forEach(item -> {
                dynamoDbTable.putItem(r -> r.item(item));
            });
            log.debug("Saved");
        } else {
            log.warn("Empty list provided");
        }
    }

    private <T> DynamoDbTable<T> tableFor(String tableName, Class<T> type) {
        TableSchema<T> schema = TableSchema.fromBean(type);
        return dbMapper.table(tableName, schema);
    }
}
