# GitHub Actions Guide

This document provides guidance on common tasks when developing and maintaining GitHub Actions and workflows in this project.

## Passing Variables Between Steps

There are two primary ways to pass data between steps in the same job:

### 1. Step Outputs (Recommended for specific values)

Use `$GITHUB_OUTPUT` to define a value in one step and reference it in another using its `id`.

**Example:**

```yaml
jobs:
  example-job:
    runs-on: ubuntu-latest
    steps:
      - name: Generate Value
        id: generate
        run: echo "my_var=hello-world" >> "$GITHUB_OUTPUT"

      - name: Use Value
        run: echo "The value was ${{ steps.generate.outputs.my_var }}"
```

### 2. Environment Variables (Recommended for job-wide access)

Use `$GITHUB_ENV` to set an environment variable that will be available to all subsequent steps in the job.

**Example:**

```yaml
jobs:
  example-job:
    runs-on: ubuntu-latest
    steps:
      - name: Set Environment Variable
        run: echo "MY_ENV_VAR=hello-world" >> "$GITHUB_ENV"

      - name: Use Environment Variable
        run: echo "The value is $MY_ENV_VAR"
```

## Passing Variables Between Jobs

To pass data between different jobs, you must use job `outputs`.

**Example:**

```yaml
jobs:
  job1:
    runs-on: ubuntu-latest
    outputs:
      job1_output: ${{ steps.step1.outputs.my_var }}
    steps:
      - name: Generate Value
        id: step1
        run: echo "my_var=hello-world" >> "$GITHUB_OUTPUT"

  job2:
    needs: job1
    runs-on: ubuntu-latest
    steps:
      - name: Use Job Output
        run: echo "The output from job1 was ${{ needs.job1.outputs.job1_output }}"
```

## Common Patterns in this Project

*   **Version Detection**: Many actions (like `maven-version-number`) output the current project version to `$GITHUB_OUTPUT`.
*   **Conditional Steps**: Use outputs like `has_changes` to skip or execute steps based on previous results (e.g., `check-changes-in-source`).
