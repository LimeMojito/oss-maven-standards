This project is a collection of Maven POMs and modules that define development standards for Lime Mojito open-source projects. It enforces coding styles, unit testing, coverage, and simplified dependency management via a Bill of Materials (BOM).

### Tech Stack
- **Language**: Java 25 (configured in `pom.xml`)
- **Build Tool**: Maven
- **Frameworks**: Spring Boot 4.1.0, AWS CDK 2.260.0
- **Boilerplate**: Project Lombok 1.18.46
- **Testing**: JUnit 5, Mockito, Localstack, Docker
- **Standards**: Checkstyle (Custom Lime Mojito standard)

## Project Structure

- `pom.xml`: Root parent POM containing global properties and module definitions.
- `library/`: The Bill of Materials (BOM) for all shared dependencies.
- `java-development/`: Base POM for Java projects, defines plugins for checkstyle, enforcer, docker, etc.
- `jar-development/`: Standard for building JAR files.
- `spring-boot-development/`: Standard for Spring Boot applications.
- `jar-lambda-development/`: Standard for AWS Lambda (Spring Cloud Function) deployments.
- `java-cdk-development/`: Standard for AWS CDK projects.
- `utilities/`: Shared utility modules (JSON, AWS, Locking, Testing).
- `docs/`: Core project documentation and standards.
- `docs/ai-skills/`: Documentation specifically for AI agent integration and setup. Git subtree from https://github.com/LimeMojito/ai-skills.

## Coding Standards & Patterns

### 1. Maven Operations
- **Selective Build**: To build a specific module and its dependents, use:
  ```bash
  mvn install -pl <module-name> -amd
  ```
- **Version Management**: Use `mvn versions:set -DprocessAllModules -DgenerateBackupPoms=false -DnewVersion=XX-SNAPSHOT` to update project versions.

### 2. Code Quality
- **Checkstyle**: Adhere to the custom checkstyle configuration at `http://standards.limemojito.com/oss-checkstyle.xml`.
- **Lombok**: Use Lombok annotations (`@Data`, `@Value`, `@Builder`, `@Slf4j`) to minimize boilerplate code.
- **Coverage**: Maintain a minimum line coverage ratio of **95%** (`0.95`). Exclude Spring configurations (`*Config`, `*Configuration`) from coverage as per `java-development/pom.xml`.

### 3. Testing Principles
- **Docker Stubs**: Use Docker images for external resources (Postgres, Localstack) during integration tests.
- **Integration Tests**: Set the Spring profile to `integration-test` when using test utilities to ensure resources are provisioned correctly.
- **Mocking**: Prefer Mockito for unit tests; use Localstack for AWS-related integration tests.
