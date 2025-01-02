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

package com.limemojito.test.sqs;

import com.limemojito.aws.sqs.LocalstackSqsConfig;
import com.limemojito.aws.sqs.SqsSenderConfig;
import com.limemojito.test.jackson.JacksonSupportConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration class for SqsSupport.
 * <p>
 * This class is responsible for configuring the necessary components for using SqsSupport in a Spring Boot application.
 * It imports the necessary configurations and performs component scanning.
 * </p>
 * <p>
 * To use SqsSupport, import this class with your application's Spring configuration and ensure the required dependencies are available.
 * </p>
 * <p>
 * This class should be used for integration testing purposes and is tagged with the {@code integration-test} profile.
 * It configures the AWS config for localstack and creates necessary SQS queues.
 * </p>
 * <p>
 * The class can leverage {@link LocalstackSqsConfig} and {@link JacksonSupportConfiguration} to configure Localstack SQS and Jackson object mappers, respectively.
 * </p>
 * <p>
 * This class also performs component scanning for classes belonging to the {@link SqsSupport} package.
 * </p>
 *
 * @see LocalstackSqsConfig
 * @see JacksonSupportConfiguration
 * @see SqsSupport
 */
@Slf4j
@Configuration
@Import({LocalstackSqsConfig.class, JacksonSupportConfiguration.class, SqsSenderConfig.class})
@ComponentScan(basePackageClasses = SqsSupport.class)
public class SqsSupportConfig {
}
