# Responding to Issues and Generating Changes

As a GitHub AI Assistant (Lime-Code-Agent), I can assist in the development process by responding to issues, PR comments, and reviews with code changes or documentation updates.

## How I Respond

When triggered, I analyze the request in the context of the repository, plan the necessary changes, implement them, and verify them (e.g., by running tests). Once complete, I provide a summary of the work done.

In this environment, my changes are automatically captured, committed to a new branch, and presented as a Pull Request for review.

## Triggering Events

I am configured to respond to the following GitHub events:

*   **Issues (Assigned)**: When an issue is assigned to `@Lime-Code-Agent`.
*   **Issue Comments**: When a comment is made on an issue that includes `@Lime-Code-Agent`.
*   **Pull Request Review Comments**: When a comment is made on a PR review that includes `@Lime-Code-Agent`.
*   **Pull Request Reviews**: When a PR review is submitted that includes `@Lime-Code-Agent`.

### Security and Access Control

To prevent unauthorized usage, I am configured to only respond to triggers from members of the `LimeMojito/developers` team.

## Examples

Here are some common ways to interact with me:

### 1. Assigning an Issue
You can simply assign an issue to `@Lime-Code-Agent`. I will read the issue description and attempt to resolve it.

**Example Title:** Add unit tests for the UserService class
**Example Body:** Please add unit tests for `UserService` in the `library` module, covering all public methods.

### 2. Commenting on an Issue
You can mention me in a comment to ask for specific tasks or refinements.

**Example Comment:**
> @Lime-Code-Agent Please update the README.md with the latest installation instructions from the docs directory.

### 3. Reviewing a Pull Request
If you need changes on a PR, you can mention me in a review or a specific line comment.

**Example PR Review Comment:**
> @Lime-Code-Agent This method seems to have a bug in handling null values. Can you fix it and add a test case?

### 4. Direct Trigger in Issue Description
Even if I'm not assigned, if `@Lime-Code-Agent` is mentioned in the issue title or body, and the issue is assigned (to anyone), I may be triggered depending on the workflow configuration.
