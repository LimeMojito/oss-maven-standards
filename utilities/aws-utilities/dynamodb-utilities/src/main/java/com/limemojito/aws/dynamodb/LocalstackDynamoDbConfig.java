/*
 * Copyright 2011-2026 Lime Mojito Pty Ltd
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
import com.limemojito.json.spring.LimeJacksonJsonConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.util.List;

/**
 * Configures the AWS config for localstack for integration testing purposes.
 * Should be imported with your application spring configuration.
 */
@Profile("integration-test")
@Configuration
@Slf4j
@Import(LimeJacksonJsonConfiguration.class)
public class LocalstackDynamoDbConfig {

    /**
     * Bean that an generate dynamodb tables.
     *
     * @param loader            Json loader to use for definition deserialization.
     * @param dynamoDBClient    Amazon DynamoDB api
     * @param tableResourceList comma separated list of table names to create.
     * @return the creator bean for programmatic use.
     */
    @Bean
    public LocalstackDynamodbTableCreator tableCreator(JsonLoader loader,
                                                       DynamoDbClient dynamoDBClient,
                                                       @Value("#{'${localstack.dynamodb.tables:}'.split(',')}") List<String> tableResourceList) {
        final LocalstackDynamodbTableCreator tableCreator = new LocalstackDynamodbTableCreator(dynamoDBClient,
                                                                                               loader);
        log.info("Tables to create: {}", tableResourceList);
        tableResourceList.stream()
                         .filter(StringUtils::hasLength)
                         .map(String::strip)
                         .forEach(tableCreator::create);
        return tableCreator;
    }

    /**
     * Amazon Dynamodb Client configured for Localstack.
     *
     * @param localStackDynamoDbUrl Localstack URL configured by {@code localstack.url }
     * @return The API client.
     */
    @Primary
    @Bean(destroyMethod = "close")
    public DynamoDbClient dynamoDBClient(@Value("${localstack.url}") URI localStackDynamoDbUrl) {
        return DynamoDbClient.builder()
                             .endpointOverride(localStackDynamoDbUrl).build();
    }

    /**
     * Amazon Dynamodb Enhanced Client configured for Localstack.
     *
     * @param dynamoDBClient DynamoDb API client to delegate to.
     * @return The Enhanced client.
     */
    @Primary
    @Bean
    public DynamoDbEnhancedClient dynamoDBEnhancedClient(DynamoDbClient dynamoDBClient) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
    }
}
