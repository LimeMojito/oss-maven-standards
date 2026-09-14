# Lime Mojito Open Source AI Configuration

# AI DIRECTIVES

- **IDEA MCP first**: Agents MUST prefer IntelliJ /IDEA MCP tools for file access, navigation, and code analysis over
  standard terminal-based file system utilities (like `cat`, `ls`, or `grep`) whenever possible. This ensures higher
  fidelity to the project's internal structure and context. Alert the user if the IDEA MCP tool is not available in java
  projects.
- ** SIGN COMMITS **: Always sign commits and tags with the default GPG key using `git commit -S`
- **FETCH AI SKILLS**: Fetch changes to the docs/ai-skills subtree from ` https://github.com/LimeMojito/ai-skills` when there are changes in this folder. Use
  `git subtree pull --prefix=docs/ai-skills --squash https://github.com/LimeMojito/ai-skills main` to pull updates from the shared
  skills repository.
- **MAINTAIN AI SKILLS**: Update or add skills in the [AI Skills Directory](./skills) as needed with information that can  improve the efficiency of token and or credits usage.
- **PUSH AI SKILL CHANGES**: Push changes to the docs/ai-skills subtree to ` https://github.com/LimeMojito/ai-skills` when there are changes in this folder. Use
  `git subtree push --prefix=docs/ai-skills https://github.com/LimeMojito/ai-skills main` to push updates to the shared
  skills repository.
- **Documentation Updates**: When updating features, ensure `CHANGELOG.md` and `Readme.md` are updated accordingly.
- **New Modules**: Follow the established parent-child hierarchy. Use `java-development` as a base for custom
  archetypes.
- **Dependency Management**: Add new shared dependencies to `library/pom.xml` under `dependencyManagement` to ensure
  version consistency across all modules.
- **AI AGENT INFORMATION**: Refer to the project specific [AI Agent Information](../../AGENTS.md) for details on how to
  interact with the AI agent.
- **AI AGENT INFORMATION UPDATE**: Always update the project's AGENTS.md and associated information in docs/ai-skills to
  keep agent information in sync with changes to the code base.

## Project Specific skills
These are skills that are specific to the current project.

- [Project Context.md](../../project-context.md)
- [CHANGELOG.md](../../CHANGELOG.md): History of changes and version updates for this project.

## Lime Mojito General Skills
- [Improving Code Coverage](./skills/improving-code-coverage.md): Process for identifying and fixing coverage gaps.
- [GitHub Actions Development](./skills/github-actions-development.md): Guidelines for working with GitHub Actions and
  workflows.
- [Maven Profiles to adjust the build](./skills/maven-profiles.md):  Maven profiles that can be used to adjust build tasks.
                        

## Lime Mojito Standards
- [AI Technology Choices](standards/01-technology-choices.md): Why we chose Junie and our approach to agentic AI.
- [AI Setup Local](standards/02-ai-setup-local.md): How to set up agents in your local development environment.
- [GitHub Setup](standards/03-github-setup.md): Configuration for GitHub Actions and agent triggers.
- [Responding to Issues](standards/04-responding-to-issues.md): How agents process issues and pull requests in this
  repo.
- [GitHub Actions Guide](standards/05-github-actions-guide.md): Guide on common GitHub Actions tasks and patterns.
- [Maven Build Guide](standards/06-maven-incremental-builds.md): Java Maven Release process details.
- [Open Source Maven Archetypes](standards/07-oss-maven-archetypes.md): Java Open Source available archetypes.
- [Open Source Standards](standards/08-oss-maven-build-standards.md): OSS standards to apply to maven builds.
- [Version Updates](standards/09-version-updates.md): Version management instructions.
- [Useful Test Utilities](standards/10-useful-test-utilities.md): Shared utility modules and how to use them.  *Very useful for testing and debugging especially with AWS integrations and localstack*
