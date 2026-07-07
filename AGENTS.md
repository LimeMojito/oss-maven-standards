# Agent Guidelines: oss-maven-standards

This document provides context and guidelines for AI coding agents (like Junie) working on the **Lime Mojito OSS Development Standard Build** project.

## Project Overview

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
- `docs/ai/`: Documentation specifically for AI agent integration and setup.
- `docs/ai/skills/`: Reusable process documentation and skill guides for agents.

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

## Agent Skills

Detailed instructions for specific tasks are located in `docs/ai/skills/`. Agents should always check this directory for relevant skill guides before starting a task.
- [Improving Code Coverage](docs/ai/skills/improving-code-coverage.md): Process for identifying and fixing coverage gaps.

## Agent Instructions

- **Documentation Updates**: When updating features, ensure `CHANGELOG.md` and `Readme.md` are updated accordingly.
- **New Modules**: Follow the established parent-child hierarchy. Use `java-development` as a base for custom archetypes.
- **Dependency Management**: Add new shared dependencies to `library/pom.xml` under `dependencyManagement` to ensure version consistency across all modules.
- **AI AGENT INFORMATION**: Refer to the [AI Agent Information](docs/ai/01-agent-information.md) for details on how to interact with the AI agent.
- **AI AGENT INFORMATION UPDATE**: Always update AGENTS.md and associated information in docs/ai to keep agent information in sync with changes to the code base.
- **AI SKILLS UPDATE**: Update or add skills in the [AI Skills Directory](docs/ai/skills/) as needed with information that can improve efficiency of token and or credits usage.

## Useful Resources
- [AI Documentation Index](docs/ai/INDEX.md)
- [Agent Skills Directory](docs/ai/skills/)
- [Technology Choices](docs/ai/00-technology-choices.md)
- [Responding to Issues with AI](docs/ai/03-responding-to-issues.md)
- [Building Maintainable Maven Projects](https://limemojito.com/maintainable-builds-with-maven/)
