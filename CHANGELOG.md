### 14.0.0

0f51603 - [maven-release-plugin] prepare release oss-maven-standards-14.0.0
29fb286 - GW-498: Shared Release Build
48a2e90 - GW-498: Specifiy shell for python command.
06b6754 - GW-498: Remove outputs.
b13532c - GW-498: Pass secrets to action.
6418597 - GW-498: Move checkout to first step in workflow.
696b745 - GW-498: Remove explicit action.yml reference.
0d20cf3 - GW-498: Relayout files
24091a0 - GW-498: Factor out environment setup.
ce1407b - Merge pull request #4 from LimeMojito/feature/GW-497
d9458de - GW-498: Move workflow to top level.
4b076ee - GW-498: Add missing include directory
03d977a - GW-498: Switched to internal URI.
50f606d - GW-498: Remove branch marker as not added yet.
1e98d69 - GW-498: Reusable workflow
a228dec - GW-497: Add signing to release.  Add docker caching to workflow.
fec6d45 - GW-497: Update to use GPG secret in organisation.
cbb623d - GW-497: Add release mode and signing with explicit key.
a247386 - GW-497: Due to CDK execing out of process we need to do a mvn install.
0748b89 - GW-497: Install CDK
7890fcf - GW-497: Added maven cache.
51bef95 - GW-497: Adjusted workflow to factor up role.   Moved AWS deployment to post integration test phase.  Documented override for key id.
b4497ad - Merge remote-tracking branch 'origin/feature/GW-497' into feature/GW-497
13e7f14 - GW-498 GitHub workflow
60e50eb - GW-498 New Gitlab role
d970903 - GW-498 Need a better name for session.
1de880d - GW-498 Try ${{ replacement.
ddd928d - GW-498: Updated variables.
ae02c2d - GW-498 Configure to talk to Lime AWS
a590767 - Added AWS Region
194de41 - Update maven.yml
50c8205 - Example Workflow for create maven
216104f - Merge remote-tracking branch 'origin/master' into feature/GW-497
190f258 - Merge pull request #1 from LimeMojito/dependabot/maven/library/org.wiremock-wiremock-3.0.3
b8ad2ed - Bump org.wiremock:wiremock from 3.0.1 to 3.0.3 in /library
4090e29 - GW-497: Update major version as changing repositories and storage.  Cleanup any references to "native" as we're ignoring that option and using SnapStart instead.
d1e2be4 - Updated readme after blog post.
6b53b72 - [maven-release-plugin] prepare for next development iteration

