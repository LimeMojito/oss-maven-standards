# Release Workflows

This document explains the GitHub Action workflows used to manage the release process for Lime Mojito OSS projects.

## OSS Maven Standards Release Build

The `oss-release-build.yml` workflow is a reusable workflow (`workflow_call`) that performs a complete release of a Maven project. It handles environment setup, version updates, deployment to Maven Central, and automated documentation updates.

### Workflow Flow

The workflow follows a sequential process to ensure a clean and consistent release:

1.  **Environment Setup**:
    *   **Git Setup**: Configures Git with the build agent's identity and signing keys.
    *   **Source Change Check**: Determines if there are any changes in the source code that require a new release. If no changes are detected, the subsequent steps are skipped.
    *   **Java Setup**: Configures the Java environment (Java 25), including code signing keys for GPG and credentials for Maven Central.
    *   **AWS Setup**: Assumes the necessary AWS IAM roles for the build process.
    *   **Docker Setup**: Initializes the Docker environment for integration tests (e.g., Localstack).

2.  **Version Management**:
    *   **Update Library Versions**: Automatically updates the project version using Maven.

3.  **Release Execution**:
    *   **Maven Release Build**: Performs the actual release using the `maven-release` action. This step typically includes:
        *   Compiling and testing the code.
        *   Signing artifacts with GPG.
        *   Deploying artifacts to the Sonatype Central Portal.
        *   Creating a Git tag for the release.

4.  **Automated Documentation**:
    *   **Update Documentation**: Uses the Junie AI agent to update `CHANGELOG.md` and `Readme.md`. It summarizes recent commits, groups related issues, and removes noise like automated commit messages.
    *   **Commit Updates**: Commits and pushes the AI-generated documentation changes back to the repository.

### Actions Used

| Action | Purpose |
| :--- | :--- |
| `lime-env-setup-git` | Configures Git identity and GPG signing keys. |
| `check-changes-in-source` | Detects if source files have changed since the last build. |
| `lime-env-setup-java` | Sets up JDK, Maven settings, and GPG keys. |
| `lime-env-setup-aws` | Configures AWS credentials and OIDC role assumption. |
| `lime-env-setup-docker` | Prepares the Docker environment for builds. |
| `maven-version-update` | Manages Maven version increments. |
| `maven-release` | Handles the Maven release lifecycle and deployment. |
| `invoke-agent` | Triggers the Junie AI agent to perform documentation tasks. |

### Concurrency and Permissions

*   **Concurrency**: The workflow is restricted to one run per repository at a time to prevent conflicts when sharing AWS accounts and release resources.
*   **Permissions**: Requires `contents: write` for tagging and documentation updates, `packages: write` for artifact deployment, and `id-token: write` for AWS OIDC authentication.
