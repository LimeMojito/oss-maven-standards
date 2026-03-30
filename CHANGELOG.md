### 14.0.0
**Key Changes (Summarized from 36 items):**
- 29fb286 - GW-498: Shared Release Build
- 48a2e90 - GW-498: Specifiy shell for python command.
- 06b6754 - GW-498: Remove outputs.
- b13532c - GW-498: Pass secrets to action.
- 6418597 - GW-498: Move checkout to first step in workflow.
- 696b745 - GW-498: Remove explicit action.yml reference.
- 0d20cf3 - GW-498: Relayout files
- 24091a0 - GW-498: Factor out environment setup.
- ... and 28 more changes.

### 13.0.4
- 837f85d - Docker waitfor
- e30b13f - Merge remote-tracking branch 'origin/master'
- 3b8caaf - Healthcheck via s3 ls

### 13.0.3
- No significant changes.

### 13.0.2
**Key Changes (Summarized from 480 items):**
- 1e3a2a2 - GW-678: Removed usage of ant export properties.  Updated docker startup time as had timing failures.
- 5f59c9c - GW-678: Disable FAIL_ON_NULL_FOR_PRIMITIVES as this is a sane default for our usages.
- 8b4170e - GW-678: Switch from starter-json to starter-jackson
- 153b2e4 - GW-678: Removing commons-logging completely breaks tests.
- a238651 - GW-678: Exclude commons-logging sneaking in with spring-boot-test-starter.
- 78fe2c7 - Merge pull request #92 from LimeMojito/feature/version-update
- dde47d5 - GW-678: Update to the latest libraries.
- e45a385 - GW-678: Docker startup corrections.  Test corrections.  Factor out validation support for testing.  General code cleanup.
- ... and 343 more changes.

### 13.0.1
- f54dc55 - Remove volumes on shutdown as well.
- 28b5251 - Add health checks to docker to stop false starts
- 44c108f - Code layout.
- 4a06148 - Vulnerability fix.
- da222bf - Clean up comments.
- 7422009 - Merge remote-tracking branch 'origin/master'
- 63daeb2 - Added fast build support to skip boot start.

### 13.0.0
- a18daea - Add docker plugin to correct order in plugins list.
- 38b859e - Updated copyright.
- 9a444b1 - updated docker compose to use docker-compose plugin with file detection in ant task.  Silenced CDK warnings.
- 4a5f50a - Add test utilities from test-utilities repository.
- 5665c97 - Update copyright
- fb5f69e - Move from spring-boot-development repository.

### 3.1.0
- 59d41ed - Updated to latest patch jar versions.  Updated copyright.

### 3.0.1
- 56ca7ac - Remove alternate deploy as breaking with maven 3_9

### 3.0.0
- 3b591a8 - FX2-374: Dependencies and polishing pre-release after integration testing.
- fcee4ce - FX2-374: Remove old logback fixed version.  Add dependencies for lambda event processing.
- 092425a - Update to libraries and plugins to support spring boot 3, java 17.

### 2.7.9
- af09f8d - Update with nexus plugin again as we want to avoid automatic releases.  Try and disable the nexus auto removal of deploy plugin.

### 2.7.8
- 9ce000e - Remove nexus plugin, attempt release to both repos

### 2.7.7
- 474e2d6 - Due to thevery slow release to central and propagation multi releases take a very long time to deploy if they are independent.  We work around this by deploying to the closed source repository in a release, followed by the "manual" release via an explicit call to the nexus staging plugin on teamcity.

### 2.7.6
- 92e9cf0 - Explicitly set release profile to use.

### 2.7.5
- ec8f796 - Deploy at end appears to fail during release cycle (not called).

### 2.7.4
- fe0235a - Merge remote-tracking branch 'origin/master'
- 39ed9dd - A better configuration to copy on release.

### 2.7.3
- 5733c99 - Deploy to lime at end of release build as OSS release can take hours to propagate.

### 2.7.2
- a03700b - Deploy at end of build.

### 2.7.1
- 534d255 - Merge remote-tracking branch 'origin/master'
- b320f22 - Enabled failsafe by default in appropriate phase.

### 2.7.0
- 50a7f97 - Updated compiler to use release over source and target.
- 5b13e29 - Rearrange plugin management so that plugins are fetched even when release profile is off.  Refactor libraries to match closed source.

### 2.6.1
- 35016fe - Cleanup release profile.

### 2.6.0
- 8aa7751 - Updated to support boot applications being open sourced.  Library updates for security vulnerabilities.  Changed release to enable the release profile - moved source and doc gen into the release profile.

### 2.5.1
- c7b571d - Lombok delombok to fire just before javadoc to avoid Intellij issues.  Confirmed javadoc jar generation.
- fe4b317 - Fallback to source directory if lombok not set.

### 2.5.0
- e09818c - Lombok to annotation processor, default dependencies.  Use spring starter logging as logging dance setup.
- e688143 - Added delombok so the javadoc plugin stops complaining.
- fb0a879 - Added a dev-build profile so we can skip signing.
- 08d6ef7 - Update versions of libraries, spring boot, cloud and awspring to latest.

### 2.0.0
- 7a54741 - Remove no fork.
- 5d9b8ca - Relax processig for lombok.
- 48aaabf - Update to version 2 to handle newer spring boot, java 11, etc.

### 1.0.1
- ee75653 - GW-5: Remove use of scm.url as its a maven property.
- 86bac19 - GW-5: Realized javadoc is not executing.  Update plugin definition and versions for javadoc and source.

### 1.0.0
- ef23152 - GW-4: Made checkstyle a property
- 5a7aade - GW-4: Cleaned up the DRY with more properties.
- 784a106 - GW-4: Doesn't release without push enabled.
- 4db8b5d - GW-4: Updated project informaiton
- 3cc734d - GW-4: Playing with release plugin
- 09f9001 - Merge branch 'master' of bitbucket.org:limemojito/oss-maven-standards
- 4d67891 - GW-4: Perform a release
- cf4964e - GW-5: Deploy to OSSRH using maven.

