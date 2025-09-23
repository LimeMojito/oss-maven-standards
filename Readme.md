# Build Charter

1. The build enforces our development standards to reduce the code review load.
2. The build must have a simple developer interface – ```mvn clean install```.
3. If the clean install passes – we can move to source Pull Request (PR).
4. PR is important, as when a PR is merged we may automatically deploy to production.
5. Creating a new project or module must not require a lot of configuration (“xml hell”).
6. A module must not depend on another running Lime Mojito module for testing.
7. Any stub resources for testing must be a docker image.
    * Postgres, Localstack, etc
8. Stubs will be managed by the build process for integration test phase.
    * ie if you’re using stubs, you’re building a failsafe maven plugin IT test case.
9. The build will handle style and code metric checks (CheckStyle, Maven Enforcer, etc) so that we do not waste time in
   PR reviews.
10. For open source, we will post to Maven Central on a Release Build.

---

# Open Source Standards For Our Maven Builds

Our very “top” level of build standards is open source and available for others to use or be inspired by:

GitHub: https://github.com/LimeMojito/oss-maven-standards

The base POM files are also available on the Maven Central Repository if you want to use our approach in your own
builds.

https://repo.maven.apache.org/maven2/com/limemojito/oss/standards/
      
---

# Maven Profiles

These profiles add capabilities to our builds or allow "quick checks" as wanted by the developer.

| Profile    | Actions                                                                         |
|------------|---------------------------------------------------------------------------------|
|            | Perform a "merge" build.  Checks and installs suitable for a release candidate. |
| fast-build | Quickly build the deliverables.  No deployments, checks, tests, etc.            |
| release    | Perform a release build with all checks enabled and deployments.                |

           
---

# Building maintainable maven projects

See [article](https://limemojito.com/maintainable-builds-with-maven/) here for why we use maven and how our the POM
model works.

---

# Maven Archetypes

Our Open Source Standards library supports the following module types (archetypes) out of the box:

| Type                     | Description                                                                                                                                                                           |
|--------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| java-development	        | Base POM used to configure deployment locations, checkstyle, enforcer, docker, plugin versions, profiles, etc. Designed to be extended for different archetypes (JAR, WAR, etc.).     | 
| jar-development	         | Build a jar file with test and docker support.                                                                                                                                        |
| jar-lambda-development	  | Build a Spring Boot Cloud Function jar suitable for lambda use with AWS dependencies added by default. Jar is shaded for simple upload. . Example in development-test/jar-lambda-poc. |
| spring-boot-development	 | Spring boot jar constructed with the base spring-boot-starter and lime mojito aws-utilities for local stack support.                                                                  |
| java-cdk-development     | Lightweight support for Java jar based CDK deployments to AWS. Example in development-test/jar-lambda-poc-cdk.                                                                        |

---

# Version Updates

* The plugin update requires manual checks as it is a report.
* Version updates automatic and are configured to skip alpha, beta, rc and old date format versions.
* maven-versions-plugin has backup poms disabled as VCS is here.

## Set a new release version

```shell
mvn versions:set -DprocessAllModules -DgenerateBackupPoms=false -DnewVersion=XX-SNAPSHOT 
```

Do a replacement in this readme file so that examples are updated to the new version.

## Report on what plugin updates are available

```shell
   mvn versions:display-plugin-updates | more

```

## Update all library versions and parent dependencies

```shell
mvn versions:update-parent -U
mvn versions:update-properties -U
mvn versions:use-latest-releases -U
```

## Github Workflow

For just running version updates on git using OSS lime mojito, there is a pre-canned workflow at
.github/actions/oss-maven-patch-version.yml that updates and creates a PR. Suggest configuring to run daily on a
repository.

See Article: https://limemojito.com/version-dependency-updates-automated-in-maven/

---

# Examples

## Maven Example pom.xml for building a JAR library

This example will do all the below with only 6 lines of extra XML in your maven pom.xml file:

* enforce your dependencies are a single java version
* resolve dependencies via the [Bill of Materials Library](./library/pom.xml)
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
