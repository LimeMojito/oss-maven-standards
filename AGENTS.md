# Agent Guidelines: oss-maven-standards

This document provides context and guidelines for AI coding agents (like Junie) working on the **Lime Mojito OSS Development Standard Build** project.

## Project Overview
[Project Context](docs/project-context.md)

## Agent Skills

Detailed instructions for specific tasks are located in `docs/ai-skills/`. Agents should always check this directory for relevant skill guides before starting a task.
- [Improving Code Coverage](docs/ai-skills/skills/improving-code-coverage.md): Process for identifying and fixing coverage gaps.

## Agent Instructions

- **Documentation Updates**: When updating features, ensure `CHANGELOG.md` and `Readme.md` are updated accordingly.
- **New Modules**: Follow the established parent-child hierarchy. Use `java-development` as a base for custom archetypes.
- **Dependency Management**: Add new shared dependencies to `library/pom.xml` under `dependencyManagement` to ensure version consistency across all modules.
- **AI AGENT INFORMATION**: Refer to the [AI Agent Information](AGENTS.md) for details on how to interact with the AI agent.
- **AI AGENT INFORMATION UPDATE**: Always update AGENTS.md and associated information in docs/ai-skills to keep agent information in sync with changes to the code base.
- **AI SKILLS UPDATE**: Update or add skills in the [AI Skills Directory](docs/ai-skills/skills) as needed with information that can improve the efficiency of token and or credits usage.

## Useful Resources
- [AI Documentation Index](docs/ai-skills/Readme.md)
- [Agent Skills Directory](docs/ai-skills/skills)
- [Technology Choices](docs/ai-skills/standards/01-technology-choices.md)
- [Responding to Issues with AI](docs/ai-skills/standards/04-responding-to-issues.md)
- [Building Maintainable Maven Projects](https://limemojito.com/maintainable-builds-with-maven/)
