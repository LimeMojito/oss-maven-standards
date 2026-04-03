# Agentic AI in Build Processes

After using [Junie](https://www.jetbrains.com/junie/) in IntelliJ for a couple of months, and going
to [QCon London 2026](https://qconlondon.com/) and seeing
a [Spotify presentation on Honk](https://qconlondon.com/presentation/mar2026/rewriting-all-spotifys-code-base-all-time),
we're curious about adding agentic coding capability to our build processes and GitHub Actions.

We'd like to:

* Have the CHANGELOG.md automatically updated on each release build (main branch deploy and maven version bump).
* Have the capability to ask the AI to comment on pull requests.
* Have the AI process an issue when assigned to @Lime-Code-Agent
* Not melt the seas with AI token subscription costs.
* Using existing Gitlab Runners for processing.
* Preferable to use off-the-shelf actions to make this happen.

## Agent Selection
- We already use Junie in the IDE, so we'll use that as a starting point.  There are [Github Actions](https://github.com/JetBrains/junie-github-action) now.
- [Claude Code](https://github.com/anthropics/claude-code-action)
- [Github Copilot](https://github.com/marketplace/actions/copilot-usage-action)

## Decision matrix
Subjective 1 -> 5 for capability values.  Costs are in USD

| Agent          | InteliJ IDE Integration | Github Actions | CLI      | Configurable for Single Seat | Subscription Cost Yearly (USD) | Notes                                                    |
|----------------|-------------------------|----------------|----------|------------------------------|--------------------------------|----------------------------------------------------------|
| Junie          | 5                       | 5              | 3 (beta) | Y                            | 110                            | Junie is AI agnositc and can adjust LLM used.            |
| Claude         | 3                       | 5              | 5        | Y                            | 204                            | Claude is primarily command and MD file driven. Popular. |
| GitHub Copilot | 4                       | 5              | 5        | Y                            | 132                            | Allows multi LLM selection(s)                            |
  
# Decision
We want multi LLM options so that reduces to CoPilot or Junie.  
Junie is already costed using the AI Pro pack as part of the IDE licensing per seat for Jetbrains All Products Pack.
We'll proceed with Junie for the workflow POC and setup as a single seat using token access.
    
