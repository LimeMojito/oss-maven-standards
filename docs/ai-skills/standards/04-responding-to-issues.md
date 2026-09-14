# Responding to Issues with AI

As an AI-driven agent (Junie), I can directly help maintain the repository by processing issues, generating code changes, and opening Pull Requests for review. This streamlines the development process by automating routine tasks and documentation updates.

## How it Works

When I am triggered by a GitHub event, I analyze the context of the issue or comment. I then:
1.  **Analyze the task**: Understand the requirements and the existing codebase.
2.  **Formulate a plan**: Identify the files to be created or modified.
3.  **Execute changes**: Apply edits or create new files in a dedicated branch.
4.  **Create a Pull Request**: Submit the changes for human review, linking them back to the original issue.

## GitHub Event Triggers

I can be triggered by several GitHub events, typically configured in the `.github/workflows` directory. Common triggers include:

- **Issue Assignment**: When an issue is assigned to me (e.g., `@Lime-Code-Agent`).
- **Issue Comments**: When I am mentioned in a comment on an existing issue.
- **Pull Request Comments**: When I am asked to review or modify code in a PR.
- **Labels**: Adding a specific label (like `ai-help`) to an issue or PR.

## Examples

### 1. Assigning a task
You can create an issue and assign it to the AI agent.
- **Issue Title**: "Update README with new profile documentation"
- **Issue Description**: "Add a section explaining the 'release' profile to Readme.md."
- **Action**: Assign to `@Lime-Code-Agent`.
- **Result**: I will create a branch, update `Readme.md`, and open a PR.

### 2. Requesting changes in a comment
If an issue is already open, you can ask for my help in the comments.
- **Comment**: "@Lime-Code-Agent please add a unit test for the `StringUtilities` class."
- **Result**: I will analyze the class, generate the test file, and submit a PR.

### 3. Documentation generation
- **Issue**: "Document our AI setup"
- **Action**: Assigned to me.
- **Result**: I can generate markdown files (like this one!) to document processes or architectures.

---
*Note: I will never push directly to protected branches like `master`. All changes are submitted via Pull Requests for your final approval.*
