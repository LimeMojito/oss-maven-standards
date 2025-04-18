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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.utils.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * The S3Support class provides methods for interacting with Amazon S3 bucket.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class S3Support {

    private final S3Client s3;

    /**
     * Initializes a new instance of the S3Support class with the specified S3 client.
     *
     * @param s3 The S3 client to be used for interacting with Amazon S3.
     */
    public S3Support(S3Client s3) {
        this.s3 = s3;
    }

    /**
     * Wipes all objects from the specified bucket.
     * Will stop after 1000 iterations to prevent infinite loops.
     *
     * @param bucketName the name of the bucket to be wiped
     */
    public void wipeBucket(String bucketName) {
        // no op if bucket exists.
        this.createBucket(bucketName);
        log.info("Wiping bucket {}", bucketName);
        ListObjectsResponse objectListing;
        int deleted = 0;
        int iterations = 0;
        final int MAX_ITERATIONS = 1000;

        do {
            objectListing = s3.listObjects(r -> r.bucket(bucketName));
            for (S3Object objectSummary : objectListing.contents()) {
                s3.deleteObject(r -> r.bucket(bucketName).key(objectSummary.key()));
                deleted++;
            }
            log.debug("Deleted {} from {}", deleted, bucketName);
            iterations++;

            if (iterations >= MAX_ITERATIONS) {
                log.warn("Reached maximum iterations ({}) while wiping bucket {}. Deleted {} objects but there may be more.",
                        MAX_ITERATIONS, bucketName, deleted);
                break;
            }
        } while (objectListing.isTruncated());

        if (iterations < MAX_ITERATIONS) {
            log.info("Wipe of {} completed. {} objects deleted.", bucketName, deleted);
        }
    }

    /**
     * Wipes all objects from the specified bucket.
     *
     * @param uri S3 URI containing the bucket name to wipe
     */
    public void wipeBucket(URI uri) {
        wipeBucket(bucket(uri));
    }

    /**
     * Uploads data to a specified location in a bucket.
     *
     * @param bucketName  the name of the bucket where the data will be uploaded.
     * @param key         the key or path of the file in the bucket.
     * @param input       the input stream containing the data to be uploaded.
     * @param inputLength the length of the input stream in bytes.
     * @param contentType the content type of the data to be uploaded.
     */
    public void putData(String bucketName, String key, InputStream input, long inputLength, String contentType) {
        log.info("Uploading {}->{} of type {}", bucketName, key, contentType);
        s3.putObject(r -> r.bucket(bucketName)
                           .key(key)
                           .contentType(contentType)
                           .contentLength(inputLength), RequestBody.fromInputStream(input, inputLength));
        log.info("Upload {}->{} completed.", bucketName, key);
    }

    /**
     * Saves the specified data to the specified URI using the specified content type.
     *
     * @param uri         the URI where the data will be saved
     * @param contentType the content type of the data
     * @param data        the data to be saved
     * @throws RuntimeException if an I/O error occurs
     */
    public void putData(URI uri, String contentType, byte[] data) {
        if (uri == null) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            putData(bucket(uri), key(uri), inputStream, data.length, contentType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to put data to S3: " + e.getMessage(), e);
        }
    }

    /**
     * Puts a classpath binary resource into the specified S3 bucket with the given key.
     * The classpath resource is identified by its resource path.
     * This method calls the putClasspathResourceAs method internally with the content type set to "application/octet-stream".
     *
     * @param bucketName            the name of the S3 bucket where the classpath binary resource should be stored
     * @param key                   the key under which the classpath binary resource will be stored
     * @param classpathResourcePath the resource path of the classpath binary resource to be stored,
     *                              relative to the classpath root
     */
    public void putClasspathBinary(String bucketName, String key, String classpathResourcePath) {
        putClasspathResourceAs(bucketName, key, classpathResourcePath, "application/octet-stream");
    }

    /**
     * Puts a classpath binary resource into the specified S3 bucket with the given key.
     * The classpath resource is identified by its resource path.
     * This method calls the putClasspathResourceAs method internally with the content type set to "application/octet-stream".
     *
     * @param s3Uri                 S3 Uri for the key (s3://bucketName/keyPath)
     * @param classpathResourcePath the resource path of the classpath binary resource to be stored,
     *                              relative to the classpath root
     */
    public void putClasspathBinary(URI s3Uri, String classpathResourcePath) {
        putClasspathResourceAs(s3Uri, classpathResourcePath, "application/octet-stream");
    }

    /**
     * Uploads a classpath resource to the specified S3 bucket with the given key and MIME type.
     *
     * @param s3Uri                 S3 Uri for the key (s3://bucketName/keyPath)
     * @param classpathResourcePath the path of the classpath resource to upload
     * @param mimeType              the MIME type of the resource
     */
    public void putClasspathResourceAs(URI s3Uri, String classpathResourcePath, String mimeType) {
        putClasspathResourceAs(bucket(s3Uri), key(s3Uri), classpathResourcePath, mimeType);
    }

    /**
     * Uploads a classpath resource to the specified S3 bucket with the given key and MIME type.
     *
     * @param bucketName            the name of the S3 bucket to upload the resource to
     * @param key                   the key (or path) under which the resource will be stored in the S3 bucket
     * @param classpathResourcePath the path of the classpath resource to upload
     * @param mimeType              the MIME type of the resource
     * @throws RuntimeException if the resource cannot be loaded from the classpath or an I/O error occurs
     */
    public void putClasspathResourceAs(String bucketName, String key, String classpathResourcePath, String mimeType) {
        byte[] dataBytes;

        // First, load the resource and convert to byte array
        try (InputStream inputStream = getClass().getResourceAsStream(classpathResourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Could not load " + classpathResourcePath + " from classpath");
            }
            dataBytes = IoUtils.toByteArray(inputStream);
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Could not load " + classpathResourcePath + " from classpath", e);
        }

        // Then, create a new input stream from the byte array and upload
        try (ByteArrayInputStream byteData = new ByteArrayInputStream(dataBytes)) {
            putData(bucketName, key, byteData, dataBytes.length, mimeType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload " + classpathResourcePath + " to S3", e);
        }
    }

    /**
     * Checks if a key exists in a given bucket.
     *
     * @param bucketName the name of the bucket
     * @param key        the key to be checked
     * @return {@code true} if the key exists in the bucket, {@code false} otherwise
     */
    public boolean keyExists(String bucketName, String key) {
        try {
            s3.headObject(r -> r.bucket(bucketName).key(key));
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    /**
     * Checks if a key exists in a given bucket.
     *
     * @param s3Uri S3 Uri for the key (s3://bucketName/keyPath)
     * @return {@code true} if the key exists in the bucket, {@code false} otherwise
     */
    public boolean keyExists(URI s3Uri) {
        return keyExists(bucket(s3Uri), key(s3Uri));
    }

    /**
     * Retrieves data from the specified bucket and key.
     *
     * @param bucketName the name of the bucket
     * @param key        the key of the object to retrieve
     * @return an InputStream of the retrieved data.  Should be closed by caller.
     */
    public InputStream getData(String bucketName, String key) {
        log.info("Retrieving {}->{}", bucketName, key);
        return s3.getObject(r -> r.bucket(bucketName).key(key), ResponseTransformer.toInputStream());
    }

    /**
     * Retrieves data from the specified bucket and key.
     *
     * @param s3Uri S3 Uri for the key (s3://bucketName/keyPath)
     * @return an InputStream of the retrieved data.  Should be closed by caller.
     */
    public InputStream getData(URI s3Uri) {
        return getData(bucket(s3Uri), key(s3Uri));
    }

    /**
     * Converts the given S3 bucket and key to an S3 URI.
     *
     * @param bucket The name of the S3 bucket.
     * @param key    The key of the object in the S3 bucket.
     * @return The S3 URI in the format "s3://bucket/key".
     * @throws IllegalArgumentException if bucket or key is null
     */
    @SneakyThrows
    public URI toS3Uri(String bucket, String key) {
        if (bucket == null) {
            throw new IllegalArgumentException("Bucket name cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        // Encode the key to handle special characters, but preserve forward slashes
        String encodedKey = key;
        if (key.contains(" ") || key.contains("?") || key.contains("&") || key.contains("=")) {
            encodedKey = java.net.URLEncoder.encode(key, "UTF-8")
                .replace("%2F", "/")  // Preserve path separators
                .replace("+", "%20"); // Replace + with %20 for spaces
        }

        return URI.create("s3://%s/%s".formatted(bucket, encodedKey));
    }

    /**
     * Creates a bucket with the given bucket name.  If the bucket already exists this method returns cleanly.
     *
     * @param uri s3 uri representing the bucket.
     * @throws IllegalArgumentException if the URI is null or doesn't contain a bucket name
     */
    public void createBucket(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("S3 URI cannot be null");
        }

        String bucketName = s3.utilities()
                             .parseUri(uri)
                             .bucket()
                             .orElseThrow(() -> new IllegalArgumentException("S3 URI does not contain a bucket name: " + uri));

        createBucket(bucketName);
    }

    /**
     * Creates a bucket with the given bucket name.  If the bucket already exists this method returns cleanly.
     *
     * @param bucketName the name of the bucket to be created
     * @throws IllegalArgumentException if the bucket name is null or empty
     */
    public void createBucket(String bucketName) {
        if (bucketName == null || bucketName.isEmpty()) {
            throw new IllegalArgumentException("Bucket name cannot be null or empty");
        }

        try {
            s3.createBucket(r -> r.bucket(bucketName));
            log.info("Created bucket s3://{}", bucketName);
        } catch (BucketAlreadyOwnedByYouException e) {
            log.info("Bucket already created.");
        } catch (Exception e) {
            log.error("Failed to create bucket {}: {}", bucketName, e.getMessage());
            throw e;
        }
    }

    /**
     * Extracts the key from an S3 URI.
     *
     * @param uri the S3 URI
     * @return the key part of the URI
     * @throws IllegalArgumentException if the URI is null or doesn't contain a key
     */
    private String key(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("S3 URI cannot be null");
        }

        return s3.utilities()
                .parseUri(uri)
                .key()
                .orElseThrow(() -> new IllegalArgumentException("S3 URI does not contain a key: " + uri));
    }

    /**
     * Extracts the bucket name from an S3 URI.
     *
     * @param uri the S3 URI
     * @return the bucket name part of the URI
     * @throws IllegalArgumentException if the URI is null or doesn't contain a bucket name
     */
    private String bucket(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("S3 URI cannot be null");
        }

        return s3.utilities()
                .parseUri(uri)
                .bucket()
                .orElseThrow(() -> new IllegalArgumentException("S3 URI does not contain a bucket name: " + uri));
    }
}
