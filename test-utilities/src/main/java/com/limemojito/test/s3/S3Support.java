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

package com.limemojito.test.s3;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Uri;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.utils.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Slf4j
@Service
public class S3Support {

    private final S3Client s3;

    public S3Support(S3Client s3) {
        this.s3 = s3;
    }

    public void wipeBucket(String bucketName) {
        log.info("Wiping bucket {}", bucketName);
        ListObjectsResponse objectListing;
        int deleted = 0;
        do {
            objectListing = s3.listObjects(r -> r.bucket(bucketName));
            for (S3Object objectSummary : objectListing.contents()) {
                s3.deleteObject(r -> r.bucket(bucketName).key(objectSummary.key()));
                deleted++;
            }
            log.debug("Deleted {} from {}", deleted, bucketName);
        } while (objectListing.isTruncated());
        log.info("Wipe of {} completed.  {} objects deleted.", bucketName, deleted);
    }

    public void putData(String bucketName, String key, InputStream input, long inputLength, String contentType) {
        log.info("Uploading {}->{} of type {}", bucketName, key, contentType);
        s3.putObject(r -> r.bucket(bucketName)
                           .key(key)
                           .contentType(contentType)
                           .contentLength(inputLength), RequestBody.fromInputStream(input, inputLength));
        log.info("Upload {}->{} completed.", bucketName, key);
    }

    @SneakyThrows
    public void putData(URI uri, String contentType, byte[] data) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            final S3Uri s3Uri = s3.utilities().parseUri(uri);
            final String bucketName = s3Uri.bucket().orElseThrow();
            final String key = s3Uri.key().orElseThrow();
            putData(bucketName, key, inputStream, data.length, contentType);
        }
    }

    public void putClasspathBinary(String bucketName, String key, String classpathResourcePath) {
        putClasspathResourceAs(bucketName, key, classpathResourcePath, "application/octet-stream");
    }

    public void putClasspathResourceAs(String bucketName, String key, String classpathResourcePath, String mimeType) {
        try (InputStream inputStream = getClass().getResourceAsStream(classpathResourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Could not load " + classpathResourcePath + " from classpath");
            }
            final byte[] dataBytes = IoUtils.toByteArray(inputStream);
            try (ByteArrayInputStream byteData = new ByteArrayInputStream(dataBytes)) {
                putData(bucketName, key, byteData, dataBytes.length, mimeType);
            }
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Could not load " + classpathResourcePath + " from classpath");
        }
    }

    public boolean keyExists(String bucketName, String key) {
        try {
            s3.headObject(r -> r.bucket(bucketName).key(key));
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    /**
     * @param bucketName bucket
     * @param key        key
     * @return inputStream - Requires close by caller.
     */
    public InputStream getData(String bucketName, String key) {
        log.info("Retrieving {}->{}", bucketName, key);
        return s3.getObject(r -> r.bucket(bucketName).key(key), ResponseTransformer.toInputStream());
    }

    public URI toS3Uri(String bucket, String key) {
        return URI.create("s3://%s/%s".formatted(bucket, key));
    }

    public void createBucket(URI uri) {
        createBucket(s3.utilities()
                       .parseUri(uri)
                       .bucket()
                       .orElseThrow());
    }

    public void createBucket(String bucketName) {
        try {
            s3.createBucket(r -> r.bucket(bucketName));
            log.info("Created bucket s3://{}", bucketName);
        } catch (BucketAlreadyOwnedByYouException e) {
            log.info("Bucket already created.");
        }
    }
}
