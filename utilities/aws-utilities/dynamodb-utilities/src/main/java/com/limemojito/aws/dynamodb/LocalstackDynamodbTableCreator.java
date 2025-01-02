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

package com.limemojito.aws.dynamodb;

import com.limemojito.json.JsonLoader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
import java.util.stream.Collectors;

import static software.amazon.awssdk.services.dynamodb.model.BillingMode.PAY_PER_REQUEST;

@RequiredArgsConstructor
@Slf4j
public class LocalstackDynamodbTableCreator {
    private final DynamoDbClient db;
    private final JsonLoader loader;

    @SneakyThrows
    public void create(String tableName) {
        if (!tableExists(tableName)) {
            CreateTableRequest createTableRequest = toCreateTableRequest(tableName);
            log.info("Creating table {} with indexes {}",
                     tableName,
                     createTableRequest.globalSecondaryIndexes()
                                       .stream()
                                       .map(GlobalSecondaryIndex::indexName)
                                       .collect(Collectors.toList()));
            db.createTable(createTableRequest);
        }
    }

    public CreateTableRequest toCreateTableRequest(String tableName) {
        final LimeDynamoDbData tableData = loadTableData(tableName);
        CreateTableRequest.Builder builder = CreateTableRequest.builder()
                                                               .tableName(tableName)
                                                               .keySchema(toDynamoKeySchema(tableData.keySchema()))
                                                               .attributeDefinitions(toDynamoAttributes(tableData.attributes()))
                                                               .billingMode(PAY_PER_REQUEST);
        if (tableData.indexes() != null && !tableData.indexes().isEmpty()) {
            // due to the table request builder marking yes if a (new) 0 sized list passed.
            builder.globalSecondaryIndexes(tableData.indexes()
                                                    .stream()
                                                    .map(this::createIndex)
                                                    .collect(Collectors.toList()));
        }
        return builder.build();
    }

    private LimeDynamoDbData loadTableData(String tableName) {
        final String tableDefinitionPath = String.format("/localstack-dynamodb/%s-table.json", tableName);
        return loader.loadFrom(tableDefinitionPath, LimeDynamoDbData.class);
    }

    private boolean tableExists(String tableName) {
        try {
            return db.describeTable(req -> req.tableName(tableName)) != null;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    private List<AttributeDefinition> toDynamoAttributes(List<LimeDynamoDbData.LimeAttributeDefinition> attributes) {
        return attributes.stream()
                         .map(la -> AttributeDefinition.builder()
                                                       .attributeName(la.attributeName())
                                                       .attributeType(la.attributeType())
                                                       .build())
                         .toList();
    }

    private List<KeySchemaElement> toDynamoKeySchema(List<LimeDynamoDbData.LimeKeySchema> keySchema) {
        return keySchema.stream()
                        .map(lk -> KeySchemaElement.builder()
                                                   .keyType(KeyType.fromValue(lk.keyType()))
                                                   .attributeName(lk.attributeName())
                                                   .build())
                        .toList();
    }

    private GlobalSecondaryIndex createIndex(LimeDynamoDbData.LimeIndexInfo ii) {
        return GlobalSecondaryIndex.builder()
                                   .indexName(ii.name())
                                   .projection(p -> p.projectionType(ProjectionType.ALL))
                                   .keySchema(toDynamoKeySchema(ii.keySchema()))
                                   .build();
    }
}
