# Skill: Improving Code Coverage

This skill provides instructions on how to identify and resolve code coverage issues in the Maven project.

## Context
The project enforces a minimum line coverage (usually 95%) using the JaCoCo Maven plugin. If a build fails due to coverage checks, follow this process.

## Process

1. **Identify the Failure**: Run the build for the specific module to confirm the failure and generate the report.
   ```bash
   mvn install
   ```

2. **Locate the Report**: Open the JaCoCo HTML report to see exactly which lines are not covered.
   - Path: `<module-name>/target/site/jacoco/index.html`

3. **Analyze Gaps**:
   - Red lines in the source view indicate uncovered code.
   - Yellow diamonds indicate partially covered branches.

4. **Add Tests**:
   - Locate the test class in `src/test/java`.
   - Add test cases that exercise the uncovered lines/branches.
   - Ensure you use appropriate assertions (AssertJ is preferred in this project).

5. **Verify**: Re-run the build to ensure coverage meets the threshold.
   ```bash
   mvn install 
   ```

## Best Practices
- **Minimal Changes**: Only add tests necessary to cover the gaps.
- **Edge Cases**: Ensure tests cover null checks, empty collections, and exception paths.
- **Lombok**: Note that Lombok-generated code is usually excluded from coverage by configuration or JaCoCo defaults.
- **Exclusions**: If certain code *should* be excluded (e.g., Spring configurations), check `java-development/pom.xml` for existing exclusion patterns.
