# Maven Archetypes

Our Open Source Standards library supports the following module types (archetypes) out of the box:

| Type                     | Description                                                                                                                                                                           |
|--------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| java-development	        | Base POM used to configure deployment locations, checkstyle, enforcer, docker, plugin versions, profiles, etc. Designed to be extended for different archetypes (JAR, WAR, etc.).     | 
| jar-development	         | Build a jar file with test and docker support.                                                                                                                                        |
| jar-lambda-development	  | Build a Spring Boot Cloud Function jar suitable for lambda use with AWS dependencies added by default. Jar is shaded for simple upload. . Example in development-test/jar-lambda-poc. |
| spring-boot-development	 | Spring boot jar constructed with the base spring-boot-starter and lime mojito aws-utilities for local stack support.                                                                  |
| java-cdk-development     | Lightweight support for Java jar based CDK deployments to AWS. Example in development-test/jar-lambda-poc-cdk.                                                                        |
