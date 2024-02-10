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

package com.limemojito.aws.dynamodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limemojito.aws.JsonLoader;
import com.limemojito.aws.ObjectMapperPrototype;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.awssdk.services.dynamodb.model.BillingMode.PAY_PER_REQUEST;

public class DynamoDbTableDataTest {

    private final ObjectMapper objectMapper = ObjectMapperPrototype.createObjectMapper();
    private final JsonLoader loader = new JsonLoader(objectMapper);

    @Test
    public void shouldProduceCreateTableRequest() throws Exception {
        final DynamoDbTableData data = loader.load("/localstack-dynamodb/example-table.json", DynamoDbTableData.class);

        final CreateTableRequest tableRequest = data.toCreateTableRequest("example");
        assertThat(tableRequest.keySchema()).hasSize(1);
        assertThat(tableRequest.keySchema().get(0).attributeName()).isEqualTo(data.getKeySchema()
                                                                                  .get(0)
                                                                                  .getAttributeName());
        assertThat(tableRequest.keySchema().get(0).keyTypeAsString()).isEqualTo(data.getKeySchema()
                                                                                    .get(0)
                                                                                    .getKeyType());
        assertThat(tableRequest.attributeDefinitions()).hasSize(2);
        assertThat(tableRequest.attributeDefinitions().get(0).attributeName()).isEqualTo(data.getAttributes()
                                                                                             .get(0)
                                                                                             .getAttributeName());
        assertThat(tableRequest.attributeDefinitions().get(0).attributeTypeAsString()).isEqualTo(data.getAttributes()
                                                                                                     .get(0)
                                                                                                     .getAttributeType());
        assertThat(tableRequest.billingMode()).isEqualTo(PAY_PER_REQUEST);

        assertThat(tableRequest.globalSecondaryIndexes()).hasSize(1);
        assertThat(tableRequest.hasGlobalSecondaryIndexes()).isTrue();
    }

    @Test
    public void shouldProduceCreateTableRequestNoGSI() throws Exception {
        final DynamoDbTableData data = loader.load("/localstack-dynamodb/example-table-no-gsi.json",
                                                   DynamoDbTableData.class);

        final CreateTableRequest tableRequest = data.toCreateTableRequest("example");
        assertThat(tableRequest.keySchema()).hasSize(1);
        assertThat(tableRequest.keySchema().get(0).attributeName()).isEqualTo(data.getKeySchema()
                                                                                  .get(0)
                                                                                  .getAttributeName());
        assertThat(tableRequest.keySchema().get(0).keyTypeAsString()).isEqualTo(data.getKeySchema()
                                                                                    .get(0)
                                                                                    .getKeyType());
        assertThat(tableRequest.attributeDefinitions()).hasSize(2);
        assertThat(tableRequest.attributeDefinitions().get(0).attributeName()).isEqualTo(data.getAttributes()
                                                                                             .get(0)
                                                                                             .getAttributeName());
        assertThat(tableRequest.attributeDefinitions().get(0).attributeTypeAsString()).isEqualTo(data.getAttributes()
                                                                                                     .get(0)
                                                                                                     .getAttributeType());
        assertThat(tableRequest.billingMode()).isEqualTo(PAY_PER_REQUEST);

        assertThat(tableRequest.globalSecondaryIndexes()).hasSize(0);
        assertThat(tableRequest.hasGlobalSecondaryIndexes()).isFalse();
    }
}
