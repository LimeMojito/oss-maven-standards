# How to use Public AI skills in a project
1. Create a docs subdirectory in your project.
2. `git subtree add --prefix=docs/public-ai-skills --squash https://github.com/LimeMojito/ai-skills main`
3. Create a README.MD file in the docs directory with the following markdown:
   ```
   # Documentation
   
   * [AI Skills Index](public-ai-skills/Readme.md)
   ```                           
4. Add a link to AGENTS.md in the root of the project to the ai-skills directory.
   ``` [AI SKILLS INDEX](docs/public-ai-skills/Readme.md) ```
5. You can use this docs directory for project skills, documentation, etc as well.
