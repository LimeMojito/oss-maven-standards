# Github Setup

## Code agent shared token

1. Generate a shared token for the code agent at https://github.com/settings/tokens
2. Add the token to the environment variable `LIME_CODE_AGENT_JUNIE_TOKEN`

## Workflow

Customized junie workflow to reject non members invoking it.

1. Custom setup in .github/workflows/lime-code-agent.yml
2. Referenced from https://github.com/JetBrains/junie-github-action
3. Generate authentication token for Lime-Code-Agent
    1. Enter Github at Lime-Code-Agent
    2. Settings | Developer Settings | Personal access tokens | Generate new token
        1. LimeCodeAgentRepoAccess
        2. Access repo information
        3. Resource Owner:  **Lime Mojito Pty Ltd**
        4. Expire 1 year
        5. All repo access
        6. Contents Permission (read only)
        7. Administration (read only)
        8. In Organisation tab
            1. Members (read only)
       9. Generate
4. Grant access to LimeCodeAgentRepoAccess fine grained token as the organisation
    1. OWNER_USER | Lime Mojito Pty Ltd | Settings | Developer Settings | Personal access tokens |
       LimeCodeAgentRepoAccess | Grant access
    2. Approve
5. Secrets and Variables | Actions |
    1. LIME_CODE_AGENT_GITHUB_TOKEN
    2. << set token value>>
    3. All Repositories

