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

