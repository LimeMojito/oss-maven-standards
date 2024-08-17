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


import java.util.List;

/**
 * A Jackson ready JSON representation of DynamoDb table metadata to work around finicky Amazon classes.
 *
 * @param keySchema  keySchema attributes.
 * @param attributes table data attributes
 * @param indexes    global secondary index attributes.
 */
public record LimeDynamoDbData(List<LimeKeySchema> keySchema,
                               List<LimeAttributeDefinition> attributes,
                               List<LimeIndexInfo> indexes) {

    /**
     * DynamoDb field data attributes.
     *
     * @param attributeName attribute name.
     * @param attributeType attribute type.
     */
    public record LimeAttributeDefinition(String attributeName, String attributeType) {
    }

    /**
     * DynamoDb key data attributes.
     *
     * @param attributeName attribute name.
     * @param keyType       type of key (HASH or RANGE).
     */
    public record LimeKeySchema(String attributeName, String keyType) {
    }

    /**
     * DynamoDb index definition.  Assumes attribute propagation ALL.
     *
     * @param name      index name.
     * @param keySchema Index key definition.
     */
    public record LimeIndexInfo(String name, List<LimeKeySchema> keySchema) {
    }
}
