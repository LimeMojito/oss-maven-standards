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

import com.limemojito.aws.dynamodb.LocalstackDynamoDbConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configures the DynamoDbSupport for simplified access to DynamoDb in Spring applications using Localstack.
 * This class should be imported in your application's Spring configuration using the {@link Import} annotation.
 * It enables the necessary dependencies and creates the required beans for DynamoDb support.
 */
@Configuration
@Import(LocalstackDynamoDbConfig.class)
@ComponentScan(basePackageClasses = DynamoDbSupport.class)
public class DynamoDbSupportConfig {
}
