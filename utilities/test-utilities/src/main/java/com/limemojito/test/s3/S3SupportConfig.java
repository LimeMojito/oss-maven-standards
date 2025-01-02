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

package com.limemojito.test.s3;

import com.limemojito.aws.s3.LocalstackS3Config;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The S3SupportConfig class is a Spring configuration class that configures the necessary beans for using the S3Support class.
 * It imports the LocalstackS3Config class to configure the AWS config for localstack for integration testing purposes.
 * It also scans the package where the S3Support class is located to enable component scanning and make the S3Support class available for injection.
 */
@Configuration
@Import(LocalstackS3Config.class)
@ComponentScan(basePackageClasses = S3Support.class)
public class S3SupportConfig {
}
