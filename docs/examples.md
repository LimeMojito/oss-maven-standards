# Examples

## Maven Example pom.xml for building a JAR library

This example will do all the below with only 6 lines of extra XML in your maven pom.xml file:

* enforce your dependencies are a single java version
* resolve dependencies via the [Bill of Materials Library](../library/pom.xml)
* Enable Lombok for easier java development with less boilerplate
* Configure CheckStyle for code style checking against our standards
  at http://standards.limemojito.com/oss-checkstyle.xml
* Configure optional support for docker images loading before integration-test phase
* Configure Project Lombok for Java Development with less boilerplate at compile time.
* Configure logging support with SLF4J
* Build a jar with completed MANIFEST.MF information including version numbers.
* Build javadoc and source jars on a release build
* Configure code signing on a release build
* Configure maven repository deployment locations (I suggest overriding these for your own deployments!)

```xml 

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>my.dns.reversed.project</groupId>
    <artifactId>my-library</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>
    <parent>
        <groupId>com.limemojito.oss.standards</groupId>
        <artifactId>jar-development</artifactId>
        <version>15.3.0</version>
        <relativePath/>
    </parent>
</project>
```

When you add dependencies, common ones that are in or resolved via our library pom.xml do not need version numbers as
they are managed by our modern Bill of Materials (BOM) style dependency setup.

## Example using the AWS SNS sdk as part of the jar:

```xml 

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>my.dns.reversed.project</groupId>
    <artifactId>my-library</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>
    <parent>
        <groupId>com.limemojito.oss.standards</groupId>
        <artifactId>jar-development</artifactId>
        <version>15.3.0</version>
        <relativePath/>
    </parent>
    <dependencies>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>sns</artifactId>
        </dependency>
    </dependencies>
</project>
```

## Debugging java lambda with localstack

Supported for testing with test-utilities and JavaSupport.class -
see [Article](https://limemojito.com/deploying-java-lambda-with-localstack/).

## Using Ant to bend maven

Using Ant to perform tasks without writing a maven plugin. If statements and simple execs.
[Article](https://limemojito.com/bending-maven-with-ant/)

## AWS Lambdas and Java

* [Graal, native and why SnapStart](https://limemojito.com/native-java-aws-lambda-with-graal-vm/)
* [SnapStart Advanced Optimisation](https://limemojito.com/optimising-aws-snapstart-and-spring-boot-java-lambdas/)
* [SnapStart and SQL](https://limemojito.com/optimising-aws-snapstart-and-spring-boot-java-lambdas/)
* [Keeping SnapStart images hot](https://limemojito.com/surprise-aws-snapstart-needs-a-new-image/)
* [Using method level security with AWS Lambda](https://limemojito.com/using-aws-cognito-api-gateway-and-spring-cloud-function-lambda-for-security-authorisation/).

## Enabling Docker for Unit Tests

Modern lambda code and tight integrations with AWS lean to unit testing logic on top of DynamoDb. SQS. etc. While Docker
is enabled by default for Integration Tests (*IT) by default, you can enable it so that docker can be used as a resource
in
Unit Tests. Note that unit tests should still fulfill "class and 1st degree of resources" approach so that assertions
are specific to the class under test.

For existing test utilities, it is required to set the Spring Profile to "integration-test" so that localstack
resources, etc, spin up as expected.

```xml
<properties>
  ...
    <docker.unit.test>true</docker.unit.test>
  ...
</properties>
```
