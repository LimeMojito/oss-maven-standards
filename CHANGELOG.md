### 2.7.0
- a636ee4 - [maven-release-plugin] prepare release oss-maven-standards-2.7.0
- 50a7f97 - Updated compiler to use release over source and target.
- 5b13e29 - Rearrange plugin management so that plugins are fetched even when release profile is off.  Refactor libraries to match closed source.
- 2eb11e3 - [maven-release-plugin] prepare for next development iteration

### 2.6.1
- 72eef12 - [maven-release-plugin] prepare release oss-maven-standards-2.6.1
- 35016fe - Cleanup release profile.
- 4a720bc - [maven-release-plugin] prepare for next development iteration

### 2.6.0
- 9123e71 - [maven-release-plugin] prepare release oss-maven-standards-2.6.0
- 8aa7751 - Updated to support boot applications being open sourced.  Library updates for security vulnerabilities.  Changed release to enable the release profile - moved source and doc gen into the release profile.
- 948eec1 - [maven-release-plugin] prepare for next development iteration

### 2.5.1
- 4826944 - [maven-release-plugin] prepare release oss-maven-standards-2.5.1
- c7b571d - Lombok delombok to fire just before javadoc to avoid Intellij issues.  Confirmed javadoc jar generation.
- fe4b317 - Fallback to source directory if lombok not set.
- 33be931 - [maven-release-plugin] prepare for next development iteration

### 2.5.0
- c329a96 - [maven-release-plugin] prepare release oss-maven-standards-2.5.0
- e09818c - Lombok to annotation processor, default dependencies.  Use spring starter logging as logging dance setup.
- e688143 - Added delombok so the javadoc plugin stops complaining.
- fb0a879 - Added a dev-build profile so we can skip signing.
- 08d6ef7 - Update versions of libraries, spring boot, cloud and awspring to latest.
- d146bcb - [maven-release-plugin] prepare for next development iteration

### 2.0.0
- 04ce724 - [maven-release-plugin] prepare release oss-maven-standards-2.0.0
- 7a54741 - Remove no fork.
- 5d9b8ca - Relax processig for lombok.
- 48aaabf - Update to version 2 to handle newer spring boot, java 11, etc.
- e914256 - [maven-release-plugin] prepare for next development iteration

### 1.0.1
- 7725e04 - [maven-release-plugin] prepare release oss-maven-standards-1.0.1
- ee75653 - GW-5: Remove use of scm.url as its a maven property.
- 86bac19 - GW-5: Realized javadoc is not executing.  Update plugin definition and versions for javadoc and source.
- 4000f6b - [maven-release-plugin] prepare for next development iteration

### 1.0.0
- 7c7e3fe - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
- ef23152 - GW-4: Made checkstyle a property
- 5a7aade - GW-4: Cleaned up the DRY with more properties.
- 22ca805 - [maven-release-plugin] rollback the release of oss-maven-standards-1.0.0
- 22109a5 - [maven-release-plugin] prepare for next development iteration
- e8ca947 - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
- 784a106 - GW-4: Doesn't release without push enabled.
- f32b1f8 - [maven-release-plugin] rollback the release of oss-maven-standards-1.0.0
- 47f9e5c - [maven-release-plugin] prepare for next development iteration
- 242d2b1 - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
- 4db8b5d - GW-4: Updated project informaiton
- 8d10734 - [maven-release-plugin] rollback the release of oss-maven-standards-1.0.0
- 837aee5 - [maven-release-plugin] prepare for next development iteration
- 58c34ae - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
- 1a87996 - [maven-release-plugin] rollback the release of oss-maven-standards-1.0.0
- b21cfe5 - [maven-release-plugin] prepare for next development iteration
- 67902c7 - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
- 3cc734d - GW-4: Playing with release plugin
- 1fe1ec8 - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
- 09f9001 - Merge branch 'master' of bitbucket.org:limemojito/oss-maven-standards
- 695e484 - [maven-release-plugin] rollback the release of oss-maven-standards-1.0.0
- 51e5344 - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
- b1a1726 - [maven-release-plugin] rollback the release of oss-maven-standards-1.0.0
- 27e07d1 - [maven-release-plugin] prepare for next development iteration
- 0d3fa5c - [maven-release-plugin] prepare release oss-maven-standards-1.0.0
- 4d67891 - GW-4: Perform a release
- cf4964e - GW-5: Deploy to OSSRH using maven.
