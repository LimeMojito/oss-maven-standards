# Build Charter

1. The build enforces our development standards to reduce the code review load.
1. The build must have a simple developer interface – ```mvn clean install```.
1. If the clean install passes – we can move to source Pull Request (PR).
1. PR is important, as when a PR is merged we may automatically deploy to production.
1. Creating a new project or module must not require a lot of configuration (“xml hell”).
1. A module must not depend on another running Lime Mojito module for testing.
1. Any stub resources for testing must be a docker image.
    * Postgres, Localstack, etc
1. Stubs will be managed by the build process for integration test phase.
    * ie if you’re using stubs, you’re building a failsafe maven plugin IT test case.
1. The build will handle style and code metric checks (CheckStyle, Maven Enforcer, etc) so that we do not waste time in
   PR reviews.
1. For open source, we will post to Maven Central on a Release Build.

# Open Source Standards For Our Maven Builds

Our very “top” level of build standards is open source and available for others to use or be inspired by:

GitHub: https://github.com/LimeMojito/oss-maven-standards

The base POM files are also available on the Maven Central Repository if you want to use our approach in your own
builds.

https://repo.maven.apache.org/maven2/com/limemojito/oss/standards/

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
        <version>14.0.21</version>
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
        <version>14.0.21</version>
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

# Archetypes

Our Open Source Standards library supports the following module types (archetypes) out of the box:

| Type                     | Description                                                                                                                                                                       |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| java-development	        | Base POM used to configure deployment locations, checkstyle, enforcer, docker, plugin versions, profiles, etc. Designed to be extended for different archetypes (JAR, WAR, etc.). | 
| jar-development	         | Build a jar file with test and docker support                                                                                                                                     |
| jar-lambda-development	  | Build a Spring Boot Cloud Function jar suitable for lambda use (java 17 Runtime) with AWS dependencies added by default. Jar is shaded for simple upload.                         |
| spring-boot-development	 | Spring boot jar constructed with the base spring-boot-starter and lime mojito aws-utilities for local stack support.                                                              |

