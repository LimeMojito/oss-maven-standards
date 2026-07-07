# Skill: GitHub Actions Development

Guidelines for AI agents working with GitHub Actions and workflows in the `oss-maven-standards` project.

## Overview

GitHub Actions in this project are used for CI/CD, release management, and automation tasks. Consistency in how variables are handled and how actions are structured is critical.

## Key Principles

1.  **Prefer Outputs for Actions**: Custom actions should almost always provide results via `outputs` using `$GITHUB_OUTPUT`.
2.  **Use Bash for Shell Steps**: Always specify `shell: bash` for cross-platform consistency in actions.
3.  **Variable Naming**: Use `kebab-case` for output names and `SCREAMING_SNAKE_CASE` for internal environment variables.

## Process: Adding or Modifying a Variable

### Passing Between Steps

*   **To Export**: `echo "variable-name=value" >> "$GITHUB_OUTPUT"`
*   **To Use**: `${{ steps.step_id.outputs.variable-name }}`
*   **Note**: Ensure the step has an `id`.

### Setting Environment Variables

*   **To Export**: `echo "VARIABLE_NAME=value" >> "$GITHUB_ENV"`
*   **To Use (Bash)**: `$VARIABLE_NAME`
*   **To Use (Workflow Context)**: `${{ env.VARIABLE_NAME }}`

## Common Actions Reference

*   `maven-version-number`: Outputs `maven-version-number`.
*   `check-changes-in-source`: Outputs `changed` (true/false).
*   `maven-release`: Outputs `lime-release-version`.

## Troubleshooting

*   **Missing Output**: Verify the step has an `id` and the variable name matches exactly (case-sensitive).
*   **Empty Variable**: Check if the command generating the value actually succeeded. Use `set -e` in bash scripts.
