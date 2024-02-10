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

package com.limemojito.aws.dynamodb;


import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import lombok.Builder;
import lombok.Value;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
@SuppressWarnings("RedundantModifiersValueLombok")
public class DynamoDbTableData {
    private List<KeySchemaElement> keySchema = new ArrayList<>();
    private List<AttributeDefinition> attributes = new ArrayList<>();
    private List<IndexInfo> indexes = new ArrayList<>();

    public CreateTableRequest toCreateTableRequest(String tableName) {
        CreateTableRequest.Builder builder = CreateTableRequest.builder()
                                                               .tableName(tableName)
                                                               .keySchema(toD2KeySchemas(keySchema))
                                                               .attributeDefinitions(toD2Attributes(attributes))
                                                               .billingMode(BillingMode.PAY_PER_REQUEST);
        if (!this.indexes.isEmpty()) {
            // due to the table request builder marking yes if a (new) 0 sized list passed.
            builder.globalSecondaryIndexes(this.indexes.stream()
                                                       .map(this::createIndex)
                                                       .collect(Collectors.toList()));
        }
        return builder.build();
    }

    private GlobalSecondaryIndex createIndex(IndexInfo ii) {
        return GlobalSecondaryIndex.builder()
                                   .indexName(ii.name)
                                   .projection(p -> p.projectionType(ProjectionType.ALL))
                                   .keySchema(toD2KeySchemas(ii.keySchema))
                                   .build();
    }

    private List<software.amazon.awssdk.services.dynamodb.model.AttributeDefinition> toD2Attributes(List<AttributeDefinition> attributes1) {
        return attributes1.stream()
                          .map(this::d2Attributes)
                          .collect(Collectors.toList());
    }

    private List<software.amazon.awssdk.services.dynamodb.model.KeySchemaElement> toD2KeySchemas(List<KeySchemaElement> keySchema1) {
        return keySchema1.stream()
                         .map(this::d2KeySchema)
                         .collect(Collectors.toList());
    }

    private software.amazon.awssdk.services.dynamodb.model.AttributeDefinition d2Attributes(AttributeDefinition attributeDefinition) {
        return software.amazon.awssdk.services.dynamodb.model.AttributeDefinition.builder()
                                                                                 .attributeName(attributeDefinition.getAttributeName())
                                                                                 .attributeType(attributeDefinition.getAttributeType())
                                                                                 .build();
    }

    private software.amazon.awssdk.services.dynamodb.model.KeySchemaElement d2KeySchema(KeySchemaElement keySchemaElement) {
        return software.amazon.awssdk.services.dynamodb.model.KeySchemaElement.builder()
                                                                              .keyType(keySchemaElement.getKeyType())
                                                                              .attributeName(keySchemaElement.getAttributeName())
                                                                              .build();
    }

    private record IndexInfo(String name, List<KeySchemaElement> keySchema) {
    }
}
