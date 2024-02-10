/*
 * Copyright 2011-2023 Lime Mojito Pty Ltd
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

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamoDbSupport {
    private final DynamoDbEnhancedClient dbMapper;

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
