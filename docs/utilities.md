# Utilities

The `utilities` module provides a collection of libraries to simplify common development tasks, including JSON processing, AWS service interactions, distributed locking, and testing.

## JSON Utilities

Provides Jackson configuration and shared JSON mapping utilities for Spring-based projects.

* **Artifact:** `com.limemojito.oss.standards:json-utilities`
* **Features:**
    * Shared `JsonLoader` for reading JSON from resources.
    * `JsonMapperPrototype` for consistent Jackson `ObjectMapper` configuration.
    * `LimeJacksonJsonConfiguration` for Spring Boot auto-configuration.

### Usage

```xml
<dependency>
    <groupId>com.limemojito.oss.standards</groupId>
    <artifactId>json-utilities</artifactId>
</dependency>
```

## AWS Utilities

A collection of modules to simplify working with AWS SDK v2.

* **Base GroupId:** `com.limemojito.oss.standards.aws`

| Module | Description |
|--------|-------------|
| `dynamodb-utilities` | Enhancements for the DynamoDB Enhanced Client and Jackson integration. |
| `lambda-utilities` | Support for Spring Cloud Function and AWS Lambda, including SnapStart optimizations. |
| `lambda-sql` | SQL support utilities for AWS Lambda environments. |
| `s3-utilities` | Simplified operations for Amazon S3. |
| `sns-utilities` | Simplified operations for Amazon SNS. |
| `sqs-utilities` | Simplified operations for Amazon SQS. |
| `ssm-utilities` | Simplified operations for Amazon SSM. |

### Usage Example (DynamoDB)

```xml
<dependency>
    <groupId>com.limemojito.oss.standards.aws</groupId>
    <artifactId>dynamodb-utilities</artifactId>
</dependency>
```

## Lock Utilities

Implementations for distributed locking across multiple instances.

* **Base GroupId:** `com.limemojito.oss.standards.lock`

| Module | Description |
|--------|-------------|
| `lock-api` | Common interface and abstractions for distributed locking. |
| `lock-dynamodb` | Distributed lock implementation using Amazon DynamoDB. |
| `lock-postgres` | Distributed lock implementation using PostgreSQL `advisory_lock`. |

### Usage Example (Postgres Lock)

```xml
<dependency>
    <groupId>com.limemojito.oss.standards.lock</groupId>
    <artifactId>lock-postgres</artifactId>
</dependency>
```

## Test Utilities

Comprehensive testing support for Lime Mojito standards.

* **Artifact:** `com.limemojito.oss.test:test-utilities`
* **Features:**
    * Reflection and DTO testing (getter/setter, canonical form).
    * AWS resource testing using Localstack (DynamoDB, SQS, SNS, S3).
    * Prometheus metrics validation.
    * Synthetic AWS event generation (e.g., S3 Events).

### Usage

```xml
<dependency>
    <groupId>com.limemojito.oss.test</groupId>
    <artifactId>test-utilities</artifactId>
    <scope>test</scope>
</dependency>
```

> **Note:** When using test utilities that provision AWS resources, ensure the Spring profile is set to `integration-test`.
