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

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.limemojito.aws.endpoint.LocalstackEndpointWrapper;
import com.limemojito.aws.endpoint.LocalstackEndpointWrapperConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configures the AWS config for localstack for integration testing purposes.  Should be imported with your application spring configuration.
 */
@Configuration
@Import(LocalstackEndpointWrapperConfig.class)
@Slf4j
@Profile("integration-test")
public class LocalstackDynamoDbConfig {
    @Primary
    @Bean(destroyMethod = "close")
    public DynamoDbClient dynamoDBClient(ObjectMapper mapper,
                                         @Value("${localstack.url}") URI localStackDynamoDbUrl,
                                         @Value("#{'${localstack.dynamodb.tables:}'.split(',')}") List<String> tableResourceList) {
        DynamoDbClient db = DynamoDbClient.builder().endpointOverride(localStackDynamoDbUrl).build();
        log.info("Tables to create: {}", tableResourceList);
        tableResourceList.stream()
                         .filter(StringUtils::hasLength)
                         .map(String::strip)
                         .forEach(resourcePath -> createTableFrom(db, mapper, resourcePath));
        return db;
    }

    @Primary
    @Bean
    public DynamoDbEnhancedClient dynamoDBEnhancedClient(DynamoDbClient dynamoDBClient) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
    }

    @Primary
    @Bean(destroyMethod = "shutdown")
    public AmazonDynamoDB amazonDynamoDB(LocalstackEndpointWrapper credentialWrapper,
                                         @Value("${localstack.url}") String localStackDynamoDbUrl) {
        return credentialWrapper.wrap(AmazonDynamoDBClientBuilder.standard(), localStackDynamoDbUrl)
                                .build();
    }

    @SneakyThrows
    private void createTableFrom(DynamoDbClient db, ObjectMapper objectMapper, String tableName) {
        final String tableDefinitionPath = String.format("/localstack-dynamodb/%s-table.json", tableName);
        final InputStream resourceAsStream = getClass().getResourceAsStream(tableDefinitionPath);
        if (resourceAsStream == null) {
            throw new IllegalArgumentException("Can't load resource " + tableDefinitionPath);
        }
        final DynamoDbTableData tableData = objectMapper.readValue(resourceAsStream, DynamoDbTableData.class);
        if (!tableExists(db, tableName)) {
            CreateTableRequest createTableRequest = tableData.toCreateTableRequest(tableName);
            log.info("Creating table {} with indexes {}",
                     tableName,
                     createTableRequest.globalSecondaryIndexes()
                                       .stream()
                                       .map(GlobalSecondaryIndex::indexName)
                                       .collect(Collectors.toList()));
            db.createTable(createTableRequest);
        }
    }

    private boolean tableExists(DynamoDbClient db, String tableName) {
        try {
            return db.describeTable(req -> req.tableName(tableName)) != null;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
