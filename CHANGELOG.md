### 2.7.7

21773d1 - [maven-release-plugin] prepare release oss-maven-standards-2.7.7
474e2d6 - Due to thevery slow release to central and propagation multi releases take a very long time to deploy if they are independent.  We work around this by deploying to the closed source repository in a release, followed by the "manual" release via an explicit call to the nexus staging plugin on teamcity.
1078293 - [maven-release-plugin] prepare for next development iteration

