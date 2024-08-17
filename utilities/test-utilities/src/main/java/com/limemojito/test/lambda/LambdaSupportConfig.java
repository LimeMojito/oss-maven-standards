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

package com.limemojito.test.lambda;

import com.limemojito.aws.localstack.lambda.LocalstackLambdaConfig;
import com.limemojito.test.jackson.JacksonSupportConfiguration;
import com.limemojito.test.s3.S3SupportConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The LambdaSupportConfig class is a Spring configuration class that configures the necessary beans
 * for using the LambdaSupport class. It imports the LocalstackLambdaConfig class, JacksonSupportConfiguration class,
 * and S3SupportConfig class to configure the required dependencies.
 * It also scans the package where the LambdaSupport class is located to enable component scanning and
 * make the LambdaSupport class available for injection.
 *
 * <p>To use the LambdaSupportConfig class, include it in your Spring configuration and apply any necessary
 * profiles.</p>
 */
@Configuration
@Import({LocalstackLambdaConfig.class, JacksonSupportConfiguration.class, S3SupportConfig.class})
@ComponentScan(basePackageClasses = {LambdaSupport.class})
public class LambdaSupportConfig {
}
