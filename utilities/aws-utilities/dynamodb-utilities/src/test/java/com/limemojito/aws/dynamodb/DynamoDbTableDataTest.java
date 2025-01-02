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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limemojito.json.JsonLoader;
import com.limemojito.json.ObjectMapperPrototype;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.awssdk.services.dynamodb.model.BillingMode.PAY_PER_REQUEST;

public class DynamoDbTableDataTest {

    private final ObjectMapper objectMapper = ObjectMapperPrototype.buildBootLikeMapper();
    private final JsonLoader loader = new JsonLoader(objectMapper);
    private final LocalstackDynamodbTableCreator creator = new LocalstackDynamodbTableCreator(null, loader);

    @Test
    public void shouldProduceCreateTableRequestWithGSI() {
        final CreateTableRequest tableRequest = creator.toCreateTableRequest("example");
        assertThat(tableRequest.keySchema()).hasSize(1);
        final KeySchemaElement keySchemaElement = tableRequest.keySchema().get(0);
        assertEqualTo(keySchemaElement, "id", "HASH");
        assertThat(tableRequest.attributeDefinitions()).hasSize(2);
        assertEqualTo(tableRequest.attributeDefinitions().get(0), "id", "S");
        assertEqualTo(tableRequest.attributeDefinitions().get(1), "time", "N");
        assertThat(tableRequest.billingMode()).isEqualTo(PAY_PER_REQUEST);

        assertThat(tableRequest.globalSecondaryIndexes()).hasSize(1);
        assertThat(tableRequest.hasGlobalSecondaryIndexes()).isTrue();

        final GlobalSecondaryIndex first = tableRequest.globalSecondaryIndexes().get(0);
        assertEqualTo(first.keySchema().get(0), "id", "HASH");
        assertEqualTo(first.keySchema().get(1), "time", "RANGE");
    }

    @Test
    public void shouldProduceCreateTableRequestNoGSI() {
        final CreateTableRequest tableRequest = creator.toCreateTableRequest("example-no-gsi");
        assertThat(tableRequest.keySchema()).hasSize(1);
        assertEqualTo(tableRequest.keySchema().get(0), "id", "HASH");
        assertThat(tableRequest.attributeDefinitions()).hasSize(2);
        assertEqualTo(tableRequest.attributeDefinitions().get(0), "id", "S");
        assertEqualTo(tableRequest.attributeDefinitions().get(1), "time", "N");
        assertThat(tableRequest.billingMode()).isEqualTo(PAY_PER_REQUEST);

        assertThat(tableRequest.globalSecondaryIndexes()).hasSize(0);
        assertThat(tableRequest.hasGlobalSecondaryIndexes()).isFalse();
    }

    private static void assertEqualTo(KeySchemaElement keySchemaElement, String name, String keyType) {
        assertThat(keySchemaElement.attributeName()).isEqualTo(name);
        assertThat(keySchemaElement.keyTypeAsString()).isEqualTo(keyType);
    }

    private static void assertEqualTo(AttributeDefinition attributeDefinition, String name, String type) {
        assertThat(attributeDefinition.attributeName()).isEqualTo(name);
        assertThat(attributeDefinition.attributeTypeAsString()).isEqualTo(type);
    }
}
