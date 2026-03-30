### 14.0.20

a5b55a7 - [Release] updated version in pom.xml
125be2f - Merge pull request #8 from LimeMojito/feature/library-update
39518fe - Merge pull request #7 from LimeMojito/feature/update-docker-plugin
7dd43a9 - Maven compiler without version confuses IntelliJ syntax highlighting.
083d131 - Libraries update.
a4c34d7 - Replaced docker compose plugin as the latest gitlab runners do not support docker-compose, but the now standard docker compose commands.  Updated localstack references to stable.  Removed version from docker compose files.  Cleaned up dependency warnings.  Removed docker waitFor to rely on docker-compose.yml health checks.
e486265 - Investigate fail on github runner.
2ff4387 - Adjust locations and documentation on use of antrun plugin.
f78bef9 - GW-523: Fixed intelij error by specifying the latest version of the compiler plugin explicitly.
d1a2732 - GW-523: Remove workflow rule from public repo creation as causing issues due to oss workflows calling other public flows.
b7d6b83 - GW-523: use ssh to check out with lime build key.
2f61cc2 - GW-523: Adjust release build to take extra mvn options optionally.  Switch java distribution to corretto.  Place checkout last step as possible in setup.
a251c8f - GW-506: Correct child module support for the maven version dance.
67fe98a - GW-506: Move environment setup to the OSS build as it is being called twice by private build.
ffaebb5 - GW-506: Remove unused outputs.  Make target dir to hold key.
c15a0e4 - GW-506: Have to set custom maven settings to user so that CDK behaves.
6998073 - GW-506: Detect default branch for release as some repos use main.
521cefd - GW-506: Switch back to all workflows as the lower level ones are checked as well.
8f36d96 - GW-506: Update actions with allowing github for things like Dependabot.
01fc9f0 - GW-506: Shakeout with more repositories converted.
12c4d87 - GW-506: More logging output and add a missing settings reference to versions maven call.
b0f6554 - GW-506: Adjust release and version maven calls so that we use a custom settings file.  Common maven calls but have to use copy/paste due to input substitution.  Quoted some values causing an error in Intellij.
8c9f459 - GW-506: Git tag more specific to the release action run number.
276e0fa - GW-497: Factor out the release build with a supplied setting.xml.  Release removes the settings.xml as part of its cleanup.
dddcb85 - GW-497: Pass maven settings file as an input.
528b9f5 - GW-497: Release build to delegate to a reusable workflow.
695c3a8 - GW-497: Update repository utility to apply a ruleset to a private repo.
eaa2378 - GW-497: Update docker actions to remove node warnings.
f35e6cb - GW-497: Update environment setup to configure docker for ARM64 platform builds.
f41522c - GW-497: Add references to master branch.
829ad76 - GW-497: Workflows to reference action by repository link.
fc9dac9 - GW-497: Update vulnerabilities
178082c - Merge remote-tracking branch 'origin/master'
265b25d - GW-497: Colourise maven output.
ba3370b - [Development] updated development version in pom.xml

