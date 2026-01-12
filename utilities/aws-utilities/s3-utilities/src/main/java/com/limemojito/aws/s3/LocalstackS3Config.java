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

package com.limemojito.aws.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;

import java.net.URI;
import java.util.List;

/**
 * Configures the AWS config for localstack for integration testing purposes.  Should be imported with your application spring configuration.
 */
@Profile("integration-test")
@Configuration
@Slf4j
public class LocalstackS3Config {
    @Primary
    @Bean(destroyMethod = "close")
    public S3Client s3Client(@Value("${localstack.url}") URI localStackS3Url,
                             @Value("#{'${localstack.s3.buckets:}'.split(',')}") List<String> bucketNameList) {
        S3Client s3Client = S3Client.builder().endpointOverride(localStackS3Url).forcePathStyle(true).build();
        log.info("Buckets to create: {}", bucketNameList);
        bucketNameList.stream()
                      .filter(StringUtils::hasLength)
                      .map(String::strip)
                      .forEach(bucket -> getCreateBucketResponse(s3Client, bucket));
        return s3Client;
    }

    private static void getCreateBucketResponse(S3Client s3Client, String bucket) {
        try {
            s3Client.createBucket(req -> req.bucket(bucket));
            log.debug("Bucket {} created", bucket);
        } catch (BucketAlreadyOwnedByYouException e) {
            log.debug("Bucket {} already exists", bucket);
        }
    }
}
