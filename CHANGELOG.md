### 13.0.2
58cfe62 - [Release] updated version in pom.xml
1e3a2a2 - GW-678: Removed usage of ant export properties.  Updated docker startup time as had timing failures.
5f59c9c - GW-678: Disable FAIL_ON_NULL_FOR_PRIMITIVES as this is a sane default for our usages.
8b4170e - GW-678: Switch from starter-json to starter-jackson
153b2e4 - GW-678: Removing commons-logging completely breaks tests.
a238651 - GW-678: Exclude commons-logging sneaking in with spring-boot-test-starter.
c720613 - [Development] updated development version in pom.xml
f3a1f9b - [Release] updated version in pom.xml
78fe2c7 - Merge pull request #92 from LimeMojito/feature/version-update
dde47d5 - GW-678: Update to the latest libraries.
e45a385 - GW-678: Docker startup corrections.  Test corrections.  Factor out validation support for testing.  General code cleanup.
38bf50c - GW-678: JSON loader to create a json mapper if no bean present.
8b740d4 - GW-678: Confirm jacoco working with unit tests.
81d0e37 - GW-678: No warning message for docker compose checks.
3a39678 - GW-678: Plugin updates.
96d9ac7 - GW-678: remove Junit 4 and upgrade github API changes.  Clean compile.
ef088eb - GW-678: Update JsonLoader to Jackson 3
1368a08 - GW-678: Update major version as part of spring boot 4 major upgrade.
32154f3 - Lime Versions: Update.
8ba7cab - Merge pull request #91 from LimeMojito/feature/version-update
d3b16ad - Lime Versions: Update.
ac8d51d - Merge pull request #90 from LimeMojito/feature/version-update
a3db224 - Lime Versions: Update.
5537f43 - Merge pull request #89 from LimeMojito/feature/version-update
021cc6d - Lime Versions: Update.
7e2b617 - [Development] updated development version in pom.xml
f5ecc89 - [Release] updated version in pom.xml
9939126 - Merge pull request #88 from LimeMojito/feature/version-update
b7842e5 - Lime Versions: Update.
5392358 - FX2-551: Always run docker upload
24feb60 - [Development] updated development version in pom.xml
5ec73db - [Release] updated version in pom.xml
bd98761 - FX2-551: Rename execution for save logs.
04d00a3 - FX2-551: Update log write and tested with multiple containers.  Quoting strategies for the win.  Update docker-compose health checks to be more modern.
3e22163 - FX2-551: More xml quoting for multiline conversion.
cf30ec2 - FX2-551: More ant manipulation to handle newlines and add container name to log handle.
3e9d6a9 - FX2-551: more path adjustments.
082b7ae - FX2-551: Tighten path on artifact upload
3b8a961 - FX2-551: Skip if docker compose skip is happening.
bb024c4 - FX2-551: Update to the latest plugins and library versions.
ea4c2de - FX2-551: Dump docker logs for test output to target dir.  Update checkout version action.  Update github builds so that docker logs are kept as an artifact.
10651b5 - Merge pull request #87 from LimeMojito/feature/version-update
ffc0014 - Lime Versions: Update.
d1292dc - Enable new docker cache.
b174095 - Re-enable separate cache as otherwise we need to checkout code.
c3e68cb - Use the maven cache in setup-java.  Update version of setup java.
7a2b6d1 - Merge remote-tracking branch 'origin/master'
79b43e1 - Maven cache to use repository id rather than always being remade on a pom.xml change.
29530e9 - Merge pull request #86 from LimeMojito/feature/version-update
19742a0 - Lime Versions: Update.
c356dc2 - [Development] updated development version in pom.xml
8ff27de - [Release] updated version in pom.xml
681012d - Merge pull request #85 from LimeMojito/feature/version-update
e2983a0 - Lime Versions: Update.
f5fe45f - Merge pull request #84 from LimeMojito/feature/version-update
0417131 - Lime Versions: Update.
0ea2b27 - Updated for new blog article
afd5206 - Merge pull request #83 from LimeMojito/feature/version-update
5d061d1 - Lime Versions: Update.
6bd616b - Merge pull request #82 from LimeMojito/feature/version-update
22864e0 - Lime Versions: Update.
e06f588 - [Development] updated development version in pom.xml
b8ec566 - [Release] updated version in pom.xml
4e51773 - Fix for SQS modulith events
ab30851 - Add permissions for security checks.
da45130 - Add permissions for security checks.
f71c97c - Add permissions for security checks.
08e28cf - Remove test classpath from json utilities.
4661957 - Switch default name to artifactId.  Add explicit dependency for spring-modulith-events-aws-sns as its currently a SNAPSHOT in the BOM.
1ae4f5c - Import modulith in attempt to remove SNAPSHOT complaint on publish.
6eae695 - After central release failure, reformat all after adding name to all poms.  Updated versions to latest.  Updated plugins to latest.
da28e77 - Version update once a week
22db72c - Merge pull request #81 from LimeMojito/feature/version-update
62744fd - Lime Versions: Update.
a130dd7 - Merge pull request #80 from LimeMojito/feature/version-update
c5afd36 - Lime Versions: Update.
41f9e8b - Merge pull request #79 from LimeMojito/feature/version-update
88f6c1b - Lime Versions: Update.
f90b21a - Merge pull request #78 from LimeMojito/feature/version-update
47d36a3 - Lime Versions: Update.
ab825be - Merge pull request #77 from LimeMojito/feature/version-update
fc3c339 - Lime Versions: Update.
4a3a693 - Merge pull request #76 from LimeMojito/feature/version-update
4879c61 - Lime Versions: Update.
ce67355 - Merge pull request #75 from LimeMojito/feature/version-update
437cbef - Lime Versions: Update.
c95e691 - Merge pull request #74 from LimeMojito/feature/version-update
9159124 - Lime Versions: Update.
963a4ca - Merge pull request #73 from LimeMojito/feature/version-update
84eda56 - Lime Versions: Update.
3ccade0 - Merge pull request #72 from LimeMojito/feature/version-update
09e4ba2 - Lime Versions: Update.
f0a841e - Merge pull request #71 from LimeMojito/feature/version-update
2c1e28a - Lime Versions: Update.
2c5e481 - Merge pull request #70 from LimeMojito/feature/version-update
9524fcd - Lime Versions: Update.
3ab4c69 - Merge pull request #69 from LimeMojito/feature/version-update
0bc17dd - Lime Versions: Update.
abb83c2 - Merge pull request #68 from LimeMojito/feature/version-update
44cc0f4 - Lime Versions: Update.
d698ecf - Merge pull request #67 from LimeMojito/feature/version-update
684e7b5 - Lime Versions: Update.
b72a43a - Merge pull request #66 from LimeMojito/feature/version-update
ceba5df - Lime Versions: Update.
22197e6 - Merge pull request #65 from LimeMojito/feature/version-update
fe2ea49 - Lime Versions: Update.
5713442 - Merge pull request #64 from LimeMojito/feature/version-update
67b98be - Lime Versions: Update.
0e296dd - Merge pull request #63 from LimeMojito/feature/version-update
9b7595e - Lime Versions: Update.
5edb97e - Merge pull request #62 from LimeMojito/feature/version-update
43b0d6b - Lime Versions: Update.
690eb26 - Merge pull request #61 from LimeMojito/feature/version-update
752e670 - Lime Versions: Update.
cb2366e - Merge pull request #60 from LimeMojito/feature/version-update
672ea75 - Lime Versions: Update.
e57d3da - Merge pull request #59 from LimeMojito/feature/version-update
374a189 - Lime Versions: Update.
d8939ca - Merge pull request #58 from LimeMojito/feature/version-update
1db4531 - Lime Versions: Update.
ab6955c - Merge pull request #57 from LimeMojito/feature/version-update
0e4b78a - Lime Versions: Update.
a06ce51 - Merge pull request #56 from LimeMojito/feature/version-update
08d5aa4 - Lime Versions: Update.
cbb9290 - Merge pull request #55 from LimeMojito/feature/version-update
5178168 - Lime Versions: Update.
7e37886 - Merge pull request #54 from LimeMojito/feature/version-update
68e092b - Lime Versions: Update.
3c93828 - Merge pull request #53 from LimeMojito/feature/version-update
98ef44c - Lime Versions: Update.
ecc86d0 - Merge pull request #52 from LimeMojito/feature/version-update
75a0168 - Lime Versions: Update.
34738e5 - Merge pull request #51 from LimeMojito/feature/version-update
e73b20b - Lime Versions: Update.
229caf4 - Merge pull request #50 from LimeMojito/feature/version-update
24c16aa - Lime Versions: Update.
17ec60c - Merge pull request #49 from LimeMojito/feature/version-update
1062dc9 - Lime Versions: Update.
cbf22a8 - Merge pull request #48 from LimeMojito/feature/version-update
5aa61ea - Lime Versions: Update.
2528462 - Merge pull request #47 from LimeMojito/feature/version-update
4beee22 - Lime Versions: Update.
e0d1186 - Merge pull request #46 from LimeMojito/feature/version-update
5c3c22a - Lime Versions: Update.
a1c0738 - Merge pull request #45 from LimeMojito/feature/version-update
bb54d43 - Lime Versions: Update.
3764cc1 - Merge pull request #44 from LimeMojito/feature/version-update
2084dfe - Lime Versions: Update.
110c7b8 - Merge pull request #43 from LimeMojito/feature/version-update
31623f9 - Lime Versions: Update.
b80ca3d - Merge pull request #42 from LimeMojito/feature/version-update
17d63fc - Lime Versions: Update.
f87d04a - Merge pull request #41 from LimeMojito/feature/version-update
77ae2ff - Lime Versions: Update.
8a67829 - Merge pull request #40 from LimeMojito/feature/version-update
7f5e2d9 - Lime Versions: Update.
d8b2aac - Cleanup documentation
09e0d14 - Disabled docker cache as not working with cache layer anymore (unmaintained).
d0f4a84 - Merge pull request #39 from LimeMojito/feature/version-update
04d0d7a - Lime Versions: Update.
3a9b60f - [Development] updated development version in pom.xml
a509557 - [Release] updated version in pom.xml
3c132b6 - Merge pull request #38 from LimeMojito/feature/version-update
5c3ad8b - Lime Versions: Update.
67f4751 - Merge pull request #37 from LimeMojito/feature/version-update
5d8b695 - Lime Versions: Update.
b7475e5 - GW-640: Disable wait until published as the wait is over ten minutes!
7d1b92a - [Development] updated development version in pom.xml
8eba6a8 - [Release] updated version in pom.xml
cecf140 - GW-640: Update deployment to the central repository rather than the OSSRH.  Update minor version, update plugins.  Update readme for versions set instructions.
9e1a51a - Merge pull request #36 from LimeMojito/feature/version-update
897b3f2 - Lime Versions: Update.
1a1a9d9 - [Development] updated development version in pom.xml
4ad9b45 - [Release] updated version in pom.xml
70e0390 - Merge remote-tracking branch 'origin/master'
52fc94b - Reduce invocations to once every 10 days.
c5d1260 - Merge pull request #35 from LimeMojito/feature/version-update
0ce7e9e - Add an example for keeping a SnapStart version hot.
e99a675 - Lime Versions: Update.
5ca57cf - Fixes from build
198d80c - Merge remote-tracking branch 'origin/feature/version-update'
e5e0742 - Merge remote-tracking branch 'origin/feature/version-update'
254c254 - Lime Versions: Update.
9e03c06 - Experiment with Juli to "Find and fix coding errors in codebase."  Some null argument handling, etc.
417366f - Lime Versions: Update.
4b51599 - Merge pull request #33 from LimeMojito/feature/version-update
88cbdf5 - Lime Versions: Update.
e8f8702 - [Development] updated development version in pom.xml
22d51fb - [Release] updated version in pom.xml
23b131e - Merge pull request #32 from LimeMojito/feature/version-update
bf8e4ad - Merge branch 'master' into feature/version-update
c5bd46b - FX2-531: Clean build.
646c785 - FX2-531: SQS and SNS support to have exists, unsubscribe, destroy methods and compatibility with REAL AWS (IAM roles for subscriptions) so we can use them in system tests.  SQS destroy also cleans up DLQ as required.  SNS destroy unsubscribes any subscriptions remaining.
6fe1696 - Lime Versions: Update.
3ba8cb4 - [Development] updated development version in pom.xml
9beee02 - [Release] updated version in pom.xml
a0d2cbc - FX2-531: Enable method parameters on compile.
4556070 - Merge pull request #31 from LimeMojito/feature/version-update
f66b3ae - Lime Versions: Update.
0420656 - Merge pull request #30 from LimeMojito/feature/version-update
c7d8920 - Lime Versions: Update.
d652b50 - Merge pull request #29 from LimeMojito/feature/version-update
eb31458 - Lime Versions: Update.
27ded95 - Merge pull request #28 from LimeMojito/feature/version-update
eba9364 - Lime Versions: Update.
e4d281e - Merge pull request #27 from LimeMojito/feature/version-update
0b1b228 - Lime Versions: Update.
b8bce95 - [Development] updated development version in pom.xml
8499cca - [Release] updated version in pom.xml
0c2e70a - Merge pull request #26 from LimeMojito/feature/a-feature
cbf066a - FX2-525: switch from draft or assigned for review.
0d08258 - FX2-525: Build on open pull request open
686ab9d - Merge branch 'master' into feature/a-feature
6fd0ef4 - FX2-525: BUild on open pull request.
e219367 - FX2-525: Remove json-utilities from explicit  test classpath
e1bade3 - Merge pull request #24 from LimeMojito/feature/FX2-525-oss-jwt-security
65dd8e9 - Merge branch 'master' into feature/FX2-525-oss-jwt-security
f49da04 - FX2-525: Api Gateway Context to wrap event and authentication information into an object that can be added to a function decoration.  Added ApiGatewayPrincipal to represent the base data from cognito (JWT IDP).  Added access to raw JWT, claims from the http event and the event so we don't need to parse the JWT ourselves unless truly necessary.  Updated tests. Json helper method to convert to map.
a539528 - Merge pull request #23 from LimeMojito/feature/version-update
8ed4399 - Lime Versions: Update.
3103ca2 - FX2-525: Authentication mapping from API Gateway data.  Added tests for various API gateway events.  Enabled coverage testing.
66c190d - FX2-525:  Rework exception handler to be pluggable and support spring security exception mapping to a 403.  Switch to JsonLoader for json manipulation.  Allow override of exception mapper if another bean is present.  Add unit test with docker.  Refactor configuration to a configuration class for decorator factory.  Draft authentication manager pre claims check.
60b87ca - Merge pull request #22 from LimeMojito/feature/version-update
9d39341 - Lime Versions: Update.
7e97ef0 - Merge pull request #21 from LimeMojito/feature/version-update
c0f268f - Lime Versions: Update.
4f4acf2 - Merge remote-tracking branch 'origin/master'
d74b9f0 - Remove faulty restore key
090fef6 - [Development] updated development version in pom.xml
08f81a6 - [Release] updated version in pom.xml
a2986b8 - Merge pull request #20 from LimeMojito/feature/version-update
0cb3dc9 - Lime Versions: Update.
e122981 - Merge pull request #19 from LimeMojito/feature/version-update
a1e066e - Lime Versions: Update.
688a9a1 - Merge remote-tracking branch 'origin/master'
d460fb6 - Filter PRs on ready to review.
81ffe5c - Merge pull request #18 from LimeMojito/feature/version-update
765c19f - Lime Versions: Update.
2f22e54 - Add support to create topic from ARN and return topic name.
8d3e06e - [Development] updated development version in pom.xml
93bb235 - [Release] updated version in pom.xml
1d137bd - Add support for docker in unit tests, with the caveat that for spring testing "integration-test" profile would be required for localstack, etc.
f104cf7 - Merge pull request #17 from LimeMojito/feature/version-update
494d669 - Lime Versions: Update.
e4f7c47 - [Development] updated development version in pom.xml
59dc5d0 - [Release] updated version in pom.xml
8d17394 - Merge pull request #16 from LimeMojito/feature/version-update
1c41acb - Lime Versions: Update.
a7ee6af - Update versions to include parent versions.  Stop backup poms being generated by default.
5f7bf0e - Merge pull request #15 from LimeMojito/feature/version-update
0bbbcdc - Lime Versions: Update.
3623481 - Merge pull request #14 from LimeMojito/feature/version-update
6d5d1a0 - Lime Versions: Update.
ed5d3d9 - Further split to skip AWS for version check.
8413d92 - Separate out env setup so docker can be skipped for version checks.
b5b5b10 - [Development] updated development version in pom.xml
fed31dc - [Release] updated version in pom.xml
e86d0af - Merge pull request #13 from LimeMojito/feature/version-update
dc1daa9 - Merge branch 'master' into feature/version-update
38f76b9 - Draft so that workflow merge build triggering is easy from portal.
eff8637 - Lime Versions: Update.
dcede48 - Adjust GITHUB token to allow write for PR.
8c09ed7 - Adjust GITHUB token to allow write for PR.
a3c7137 - Adjust triggers
d946879 - Adjust naming and description
94b4002 - Add on demand.
542ae8d - Version workflow draft
5f4b657 - Whitespace fix
39b46e5 - Whitespace adjustment
50b9468 - Updated versions information.
c30356c - Adjust version rules to skip common "bad" versions.
e388d9d - Added property update for versions.
7afd279 - Merge remote-tracking branch 'origin/master'
a49a62b - Update cache GitHub actions to be "less pinned" to version.
6e78270 - [Development] updated development version in pom.xml
640914a - [Release] updated version in pom.xml
6070509 - CDK now angry if the main class is missing.  Update cdk task to skip of no source code.
491f147 - Added stream conversion functions.
2caf162 - Update patch versions
b1c68b2 - Json loader to be more useful for functional programming - simple conversions (parsing) from String to Jackson type.  Exceptions as runtime to use with Spring Cloud Function classes.
2123cf0 - Update version for (simple) CDK support.
52cc53a - Correct additivity in log configurations.
aa44d95 - Lift simple CDK deployment to an archetype (so we can raise the font bug for Java 21 runtime with amazon).  Cleaned up git ignore and clean to eat cdk.out directories.
8b94326 - Copyright Update
a95748b - [Development] updated development version in pom.xml
210832e - [Release] updated version in pom.xml
e683274 - Merge pull request #12 from LimeMojito/feature/library-update-keys
76db4e2 - Update version number due to major spring release.
16e6fe5 - Fix mockito warnings for java 21 using https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#0.3
852c942 - Attach new key and passphrase to build process.
1133a29 - Update libraries and plugins.
9672fc1 - [Development] updated development version in pom.xml
68513f0 - [Release] updated version in pom.xml
10d04ba - SQL Connection replacement for Lambda snapstart use.
cdc63ba - SQL Connection replacement for Lambda snapstart use.
01262a8 - FX2-445: Update Readme.md to cover new articles on limemojito.com
dbea643 - FX2-445: Remove pip update as github runner is complaining
19ca780 - FX2-445: Noted complaints from byte buddy in mockito.
55c8c43 - FX2-445: Correct release snafu.
27d7e2a - FX2-445: Correct release snafu.
d0970af - FX2-445:Updated Readme.md
0234924 - FX2-445: Reduce minimum maven version.
a6301c9 - FX2-445: docker cache version update.
19e3271 - FX2-445: Java 21 Support on actions
a8d7c91 - Merge remote-tracking branch 'origin/master'
bf54203 - FX2-445: Java 21 Support.
964337c - [Development] updated development version in pom.xml
1946c31 - [Release] updated version in pom.xml
4e5b453 - FX2-450: Fix release build so we sign poms as well.
7657e95 - FX2-450: up version minor due to library and behaviour changes,
f0fec6a - FX2-450: Library updates.
78af1ce - FX2-450: Adjust properties and skips.  Release profile no longer necessary for javadoc and source generation.  Sign all jars.
4a2d44d - FX2-450: Update profiles so that source, javadoc and signing are part of a normal build.
4c82de0 - [Development] updated development version in pom.xml
3cf8af3 - [Release] updated version in pom.xml
08b5168 - FX2-450: SnapStart optimisation using CRaC lifecycle.
ee4858a - Merge remote-tracking branch 'origin/master'
85921b4 - FX2-450: SnapStart optimisation using CRaC lifecycle.
03e56ed - Merge pull request #11 from LimeMojito/feature/get-lambda-from-maven
0810fea - GW-506: Checkstyle corrections.
d33f69c - GW-506: Deploy lambda from a downloaded version in the maven local repository.
ca1f7da - GW-506: Refactor ready for maven dep version
b8371dc - GW-506: Confirm functionality with POST event sample.
61b3c27 - GW-506: Working deploy
c765d51 - Merge remote-tracking branch 'origin/master'
0387fa8 - GW-506: Adjust the lambda memory settings for localstack.
7f36d49 - GW-506: Adjust the debug timeout to be maximum for lambda.
fa37894 - [Development] updated development version in pom.xml
53b34ae - [Release] updated version in pom.xml
e4c16fa - GW-506: Adjust the name not the key.
ab0e19d - GW-506: Support lambda test uploads where more than one deploy of a module with difference spring cloud beans.
2ef9b5e - GW-506: Support lambda test uploads where more than one deploy of a module with difference spring cloud beans.
22dbfff - Always exclude configuration classes.
6d9bb02 - Add json defaults to boot archetype.
2fab76a - Add json defaults to boot archetype.
b600cf4 - Remove optional as using the autoconfiguration requires the starter jar to be present anyway.
a491554 - [Development] updated development version in pom.xml
15be777 - [Release] updated version in pom.xml
be900cf - Merge pull request #10 from LimeMojito/feature/utilities-reafactor
5b3ac0a - Documentation warning removal.
e8734f1 - Remove incorrect use of inheritdoc.
f964e5f - Doco fixen
7328d3e - loc documentation warnings.
4ff4fde - SSM documentation warnings.
b936fc0 - SQS documentation warnings.
087d5a6 - Adjusted for build fails.
353655e - Moved aws-utilities to their own tree.  Updated tests, removed any SDK1 leftovers in dynamodb, confirmed lambda debug logs in docker-compose with localstack.
195d6d5 - Moved lock utilities to their own tree.
333efeb - Reworked dynamo db utilities to be SDK2 only, cleaner json implementation and use JsonLoader.
a0ee34c - Updated json utilities to use spring boot jackson autoconfiguration if the configuration class is used.
320c7fe - Refactor layout to better separate utility libraries and applications made with our own framework.
df888a2 - Refactor layout to better separate utility libraries and applications made with our own framework.
d91d116 - Documentation fixes.
b714d46 - Checkstyle
5a61a0d - Updated SNS subscription as newer localstack requires ARN rather than Q URL for subscriptions.
badef37 - Always overwrite.
11c1d4f - Fix SSM property naming.
07ccfa7 - Add sender to support configuration.
547a070 - Rework SQS to have a separated sender that uses consistent attributes when sending messages.  Removed SqsAsync client.  Spring Cloud SQS no longer necessary.
3103dc0 - Configs for localstack use the one URL.
c1318a1 - [Development] updated development version in pom.xml
531073a - [Release] updated version in pom.xml
04864b2 - Merge pull request #9 from LimeMojito/feature/add-lambda-localstack
1ab07ea - Updated readme.md
b3d1c56 - Updated semantic version due to API change
5d2ee31 - Remove wiremock from test utils as prefer docker and integration tests.
e3d47f5 - Updated readme.
0c74d72 - Refactored tests and utilities to use the same ObjectMapperPrototype and json Loader.   Updated javadoc and used AI for drafting to remove all warnings.
56618b7 - Adjust the lambda POC to use LambdaSupport and have an integration tests against docker.  Remove some unused build steps that were left over from the native experiment.
b13f328 - Add lambda support functions to work with localstack.  Add example of localstack configuration for lambda debugging.  S3 Support for dynamic creation and upload of data.  Added validation support to Jackson json testing.  Added support for AWS Lambda events which are serialized differently to Jackson defaults.
263f2a3 - Set java version to 17 for jenv
35f3ea7 - Merge remote-tracking branch 'origin/master'
a145cc8 - Fix for xmlunit vulnerability.
5f5cf59 - [Development] updated development version in pom.xml
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
e7fbb4f - [Release] updated version in pom.xml
c2e2dc8 - Merge remote-tracking branch 'origin/master'
24d9ba0 - GW-510: Remove deferred close so that the deploy completes staging.
6e6a021 - Merge pull request #6 from LimeMojito/feature/gw-510
91e2255 - GW-510: Checkstyle and clean build.
1ce29c1 - GW-510: Made final.
2033236 - GW-510: Github utility program to set branch permissions, and team access.
9e6b7d5 - Update Readme.md
2eebbde - [Development] updated development version in pom.xml
cf63d2d - [Release] updated version in pom.xml
e462407 - GW-498: Manual version update.
cb83794 - GW-498: Checkout master before pushing as previous state was detached.  Release builds are always on master.
ae83606 - GW-498: Perform versions updates and commit before release build.  Release build from tag.  Push after build.
d13a6d9 - GW-498: Do not cancel in progress.
1ab171c - GW-498: Enable deferred deploy mode for releases.
ad57d0f - GW-498: Concurrency group to stop builds overlapping on the AWS account.
1c7d0ff - GW-498: handle concurrent development update during long build.
31a70d6 - GW-498: Update development version.
6e1cd2d - GW-498: Don't increment a patch twice.
dcbf68e - GW-498: Disable the deploy plugin in release builds.  Align development version with current release.  Update readme.
4b430b8 - GW-498: Make file locations output values.
7708308 - GW-498: Remove incorrect key and rework release build not to commit temporary files.
199e958 - Merge remote-tracking branch 'origin/master'
d552e4a - GW-498: Remove incorrect key.
97c52dd - GW-498: Remove incorrect key.
9d1a2f0 - GW-498: Fix tag parameters
279ea0e - GW-498: Fix tag parameters
b53d6f5 - GW-498: Fix tag parameters
72086b1 - GW-498: Fix tag parameters
ca2dd7c - GW-498: Not using release plugin as commit work is broken with tags not passable into maven.
04e4866 - GW-498: Not using release plugin as commit work is broken with tags not passable into maven.
5fd5b79 - [maven-release-plugin] prepare for next development iteration
44e673a - [maven-release-plugin] prepare for next development iteration
751f50b - [maven-release-plugin] prepare release oss-maven-standards-14.0.11
d9a8686 - [maven-release-plugin] prepare release oss-maven-standards-14.0.11
0b32f69 - GW-498: Remove Old Bouncy castle.
053b93d - GW-498: Remove Old Bouncy castle.
97dbf1d - [Development] updated development version in pom.xml
50bb528 - [Development] updated development version in pom.xml
e3de288 - [Release] updated version in pom.xml
d3f5225 - [Release] updated version in pom.xml
caf20b7 - Merge remote-tracking branch 'origin/master'
7112bf6 - Merge remote-tracking branch 'origin/master'
69dd8f0 - GW-498: Always remove keys.  Fix repository ID.
8438859 - GW-498: Always remove keys.  Fix repository ID.
d0d14d4 - [Release] updated version in pom.xml
c723ba0 - [Release] updated version in pom.xml
b87f178 - Merge remote-tracking branch 'origin/master'
eeef33f - Merge remote-tracking branch 'origin/master'
3e11053 - GW-498: Add build number override.  GitHub substitution for git connect URL not bash substitution.
8aa3613 - GW-498: Add build number override.  GitHub substitution for git connect URL not bash substitution.
350b923 - [Release] updated version in pom.xml
576ca08 - [Release] updated version in pom.xml
8aeff2b - Merge remote-tracking branch 'origin/master'
dbbd7e9 - Merge remote-tracking branch 'origin/master'
79656de - GW-498: Remove multiline maven
a08c32d - GW-498: Remove multiline maven
09bed7c - [Release] updated version in pom.xml
e425b3b - [Release] updated version in pom.xml
2ceb9e7 - Merge remote-tracking branch 'origin/master'
1253d25 - Merge remote-tracking branch 'origin/master'
5be1e16 - GW-498: Remove echo
a7eb5bf - GW-498: Remove echo
4a629aa - [Release] updated version in pom.xml
a1eb78b - [Release] updated version in pom.xml
7ea5d94 - Merge remote-tracking branch 'origin/master'
a811b7a - Merge remote-tracking branch 'origin/master'
9fec3ad - GW-498: Debug mvn call.
24eec6b - GW-498: Debug mvn call.
17c1c90 - [Release] updated version in pom.xml
aad6ca6 - [Release] updated version in pom.xml
cae66d5 - Merge remote-tracking branch 'origin/master'
f2c7c75 - Merge remote-tracking branch 'origin/master'
56b147b - GW-498: Output correct variable.
c6cd814 - GW-498: Output correct variable.
8ea1293 - [Release] updated version in pom.xml
7681848 - [Release] updated version in pom.xml
183cfa4 - GW-498: Typo
5dc01c7 - GW-498: Typo
c64ac60 - Merge remote-tracking branch 'origin/master'
711716a - Merge remote-tracking branch 'origin/master'
cf5b58c - GW-498: Adjust GIT setup so connection URL is passed to release:perform.
784af35 - [Release] updated version in pom.xml
d81e8fe - [Release] updated version in pom.xml
10d5448 - GW-498: Signed push is not supported.
6679851 - GW-498: Tested locally with https url.
45bc5c2 - GW-498: Sed to use input without -i
dff88d2 - GW-498: Sed to use input.
f516bac - GW-498: Sed to transform.
5343410 - GW-498: Better fetch origin
08fb536 - GW-498: Switch orgin to SSH based.
4d60aa9 - GW-498: More git madness
e1f82b9 - GW-498: Add git access key back.
1ce274f - GW-498: Use import action.
71beb1b - GW-498: Set git commit key by env variable.
43a3012 - GW-498: Set git commit key.
5ced2f4 - Merge remote-tracking branch 'origin/master'
d9ebe53 - GW-498: Switched to in process as docker in docker won't work with build.  Version update using build helper, commit, then release:perform.
29b780d - [maven-release-plugin] prepare for next development iteration
5d45128 - [maven-release-plugin] prepare release oss-maven-standards-14.0.1
5b6ca2e - GW-498: Use an action for release build.
6aeeacf - GW-498: Remove copy paste error.
4620d86 - GW-498: Import LimeBuildAgent keys to do the release.
40508a3 - GW-498: Preparation goals to clean.
c920cef - GW-498: Rename repository workflows.
271bc09 - Merge pull request #5 from LimeMojito/feature/GW-499
67513a9 - GW-498: Fold release configuration into release plugin setup.  Clean up OSSRH credentials.
e9bd924 - GW-498: Update versions for node deprecation.
9c2de25 - GW-498: Shared Release Build
7c843f2 - [maven-release-plugin] prepare for next development iteration
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
1e92ece - [maven-release-plugin] prepare release oss-maven-standards-13.0.4
837f85d - Docker waitfor
e30b13f - Merge remote-tracking branch 'origin/master'
3b8caaf - Healthcheck via s3 ls
340437b - [maven-release-plugin] prepare for next development iteration
a5228a6 - [maven-release-plugin] prepare release oss-maven-standards-13.0.3
023634a - [maven-release-plugin] prepare for next development iteration
bf0a5f3 - [maven-release-plugin] prepare release oss-maven-standards-13.0.2
a720a4e - [maven-release-plugin] prepare for next development iteration

### 13.0.1
c4a136e - [maven-release-plugin] prepare release oss-maven-standards-13.0.1
f54dc55 - Remove volumes on shutdown as well.
28b5251 - Add health checks to docker to stop false starts
44c108f - Code layout.
4a06148 - Vulnerability fix.
da222bf - Clean up comments.
7422009 - Merge remote-tracking branch 'origin/master'
63daeb2 - Added fast build support to skip boot start.
0f372f7 - [maven-release-plugin] prepare for next development iteration

### 13.0.0
52293be - [maven-release-plugin] prepare release oss-maven-standards-13.0.0
a18daea - Add docker plugin to correct order in plugins list.
38b859e - Updated copyright.
9a444b1 - updated docker compose to use docker-compose plugin with file detection in ant task.  Silenced CDK warnings.
4a5f50a - Add test utilities from test-utilities repository.
5665c97 - Update copyright
fb5f69e - Move from spring-boot-development repository.
5a45dad - [maven-release-plugin] prepare for next development iteration

### 3.1.0
a324853 - [maven-release-plugin] prepare release oss-maven-standards-3.1.0
59d41ed - Updated to latest patch jar versions.  Updated copyright.
b83e57a - [maven-release-plugin] prepare for next development iteration

### 3.0.1
06210e9 - [maven-release-plugin] prepare release oss-maven-standards-3.0.1
56ca7ac - Remove alternate deploy as breaking with maven 3_9
730d84e - [maven-release-plugin] prepare for next development iteration

### 3.0.0
2dbce5b - [maven-release-plugin] prepare release oss-maven-standards-3.0.0
3b591a8 - FX2-374: Dependencies and polishing pre-release after integration testing.
fcee4ce - FX2-374: Remove old logback fixed version.  Add dependencies for lambda event processing.
092425a - Update to libraries and plugins to support spring boot 3, java 17.
b8b6fd5 - [maven-release-plugin] prepare for next development iteration

### 2.7.9
9866bf2 - [maven-release-plugin] prepare release oss-maven-standards-2.7.9
af09f8d - Update with nexus plugin again as we want to avoid automatic releases.  Try and disable the nexus auto removal of deploy plugin.
1cc69b2 - [maven-release-plugin] prepare for next development iteration

### 2.7.8
70893e5 - [maven-release-plugin] prepare release oss-maven-standards-2.7.8
9ce000e - Remove nexus plugin, attempt release to both repos
5951835 - [maven-release-plugin] prepare for next development iteration

### 2.7.7
21773d1 - [maven-release-plugin] prepare release oss-maven-standards-2.7.7
474e2d6 - Due to thevery slow release to central and propagation multi releases take a very long time to deploy if they are independent.  We work around this by deploying to the closed source repository in a release, followed by the "manual" release via an explicit call to the nexus staging plugin on teamcity.
1078293 - [maven-release-plugin] prepare for next development iteration

### 2.7.6
a7e96df - [maven-release-plugin] prepare release oss-maven-standards-2.7.6
92e9cf0 - Explicitly set release profile to use.
386d27c - [maven-release-plugin] prepare for next development iteration

### 2.7.5
7d1305d - [maven-release-plugin] prepare release oss-maven-standards-2.7.5
ec8f796 - Deploy at end appears to fail during release cycle (not called).
47ad942 - [maven-release-plugin] prepare for next development iteration

### 2.7.4
f5efb02 - [maven-release-plugin] prepare release oss-maven-standards-2.7.4
fe0235a - Merge remote-tracking branch 'origin/master'
39ed9dd - A better configuration to copy on release.
77d434f - [maven-release-plugin] prepare for next development iteration

### 2.7.3
88631f7 - [maven-release-plugin] prepare release oss-maven-standards-2.7.3
5733c99 - Deploy to lime at end of release build as OSS release can take hours to propagate.
d337aeb - [maven-release-plugin] rollback changes from release preparation of my-branch
cca5c7c - [maven-release-plugin] prepare branch my-branch
d605f16 - [maven-release-plugin] prepare for next development iteration

### 2.7.2
41b7563 - [maven-release-plugin] prepare release oss-maven-standards-2.7.2
a03700b - Deploy at end of build.
8e28cc8 - [maven-release-plugin] prepare for next development iteration

### 2.7.1
8239ac6 - [maven-release-plugin] prepare release oss-maven-standards-2.7.1
534d255 - Merge remote-tracking branch 'origin/master'
b320f22 - Enabled failsafe by default in appropriate phase.
cc3d80e - [maven-release-plugin] prepare for next development iteration

### 2.7.0
a636ee4 - [maven-release-plugin] prepare release oss-maven-standards-2.7.0
50a7f97 - Updated compiler to use release over source and target.
5b13e29 - Rearrange plugin management so that plugins are fetched even when release profile is off.  Refactor libraries to match closed source.
2eb11e3 - [maven-release-plugin] prepare for next development iteration

### 2.6.1
72eef12 - [maven-release-plugin] prepare release oss-maven-standards-2.6.1
35016fe - Cleanup release profile.
4a720bc - [maven-release-plugin] prepare for next development iteration

### 2.6.0
9123e71 - [maven-release-plugin] prepare release oss-maven-standards-2.6.0
8aa7751 - Updated to support boot applications being open sourced.  Library updates for security vulnerabilities.  Changed release to enable the release profile - moved source and doc gen into the release profile.
948eec1 - [maven-release-plugin] prepare for next development iteration

### 2.5.1
4826944 - [maven-release-plugin] prepare release oss-maven-standards-2.5.1
c7b571d - Lombok delombok to fire just before javadoc to avoid Intellij issues.  Confirmed javadoc jar generation.
fe4b317 - Fallback to source directory if lombok not set.
33be931 - [maven-release-plugin] prepare for next development iteration

### 2.5.0
c329a96 - [maven-release-plugin] prepare release oss-maven-standards-2.5.0
e09818c - Lombok to annotation processor, default dependencies.  Use spring starter logging as logging dance setup.
e688143 - Added delombok so the javadoc plugin stops complaining.
fb0a879 - Added a dev-build profile so we can skip signing.
08d6ef7 - Update versions of libraries, spring boot, cloud and awspring to latest.
d146bcb - [maven-release-plugin] prepare for next development iteration

### 2.0.0
04ce724 - [maven-release-plugin] prepare release oss-maven-standards-2.0.0
7a54741 - Remove no fork.
5d9b8ca - Relax processig for lombok.
48aaabf - Update to version 2 to handle newer spring boot, java 11, etc.
e914256 - [maven-release-plugin] prepare for next development iteration

### 1.0.1
7725e04 - [maven-release-plugin] prepare release oss-maven-standards-1.0.1
ee75653 - GW-5: Remove use of scm.url as its a maven property.
86bac19 - GW-5: Realized javadoc is not executing.  Update plugin definition and versions for javadoc and source.
4000f6b - [maven-release-plugin] prepare for next development iteration

### 1.0.0
7c7e3fe - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
ef23152 - GW-4: Made checkstyle a property
5a7aade - GW-4: Cleaned up the DRY with more properties.
22ca805 - [maven-release-plugin] rollback the release of oss-maven-standards-1.0.0
22109a5 - [maven-release-plugin] prepare for next development iteration
e8ca947 - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
784a106 - GW-4: Doesn't release without push enabled.
f32b1f8 - [maven-release-plugin] rollback the release of oss-maven-standards-1.0.0
47f9e5c - [maven-release-plugin] prepare for next development iteration
242d2b1 - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
4db8b5d - GW-4: Updated project informaiton
8d10734 - [maven-release-plugin] rollback the release of oss-maven-standards-1.0.0
837aee5 - [maven-release-plugin] prepare for next development iteration
58c34ae - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
1a87996 - [maven-release-plugin] rollback the release of oss-maven-standards-1.0.0
b21cfe5 - [maven-release-plugin] prepare for next development iteration
67902c7 - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
3cc734d - GW-4: Playing with release plugin
1fe1ec8 - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
09f9001 - Merge branch 'master' of bitbucket.org:limemojito/oss-maven-standards
695e484 - [maven-release-plugin] rollback the release of oss-maven-standards-1.0.0
51e5344 - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
b1a1726 - [maven-release-plugin] rollback the release of oss-maven-standards-1.0.0
27e07d1 - [maven-release-plugin] prepare for next development iteration
0d3fa5c - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
4d67891 - GW-4: Perform a release
cf4964e - GW-5: Deploy to OSSRH using maven.
