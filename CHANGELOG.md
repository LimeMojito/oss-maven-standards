### 2.7.9
- 846783c - Add CHANGELOG for 2.7.9
- 9866bf2 - [maven-release-plugin] prepare release oss-maven-standards-2.7.9
- af09f8d - Update with nexus plugin again as we want to avoid automatic releases.  Try and disable the nexus auto removal of deploy plugin.
- 1cc69b2 - [maven-release-plugin] prepare for next development iteration

### 2.7.8
- 6034920 - Add CHANGELOG for 2.7.8
- 70893e5 - [maven-release-plugin] prepare release oss-maven-standards-2.7.8
- 9ce000e - Remove nexus plugin, attempt release to both repos
- 5951835 - [maven-release-plugin] prepare for next development iteration

### 2.7.7
- 6f876bf - Add CHANGELOG for 2.7.7
- 21773d1 - [maven-release-plugin] prepare release oss-maven-standards-2.7.7
- 474e2d6 - Due to thevery slow release to central and propagation multi releases take a very long time to deploy if they are independent.  We work around this by deploying to the closed source repository in a release, followed by the "manual" release via an explicit call to the nexus staging plugin on teamcity.
- 1078293 - [maven-release-plugin] prepare for next development iteration

### 2.7.6
- 15c0f96 - Add CHANGELOG for 2.7.6
- a7e96df - [maven-release-plugin] prepare release oss-maven-standards-2.7.6
- 92e9cf0 - Explicitly set release profile to use.
- 386d27c - [maven-release-plugin] prepare for next development iteration

### 2.7.5
- 5313b98 - Add CHANGELOG for 2.7.5
- 7d1305d - [maven-release-plugin] prepare release oss-maven-standards-2.7.5
- ec8f796 - Deploy at end appears to fail during release cycle (not called).
- 47ad942 - [maven-release-plugin] prepare for next development iteration

### 2.7.4
- bc1f07f - Add CHANGELOG for 2.7.4
- f5efb02 - [maven-release-plugin] prepare release oss-maven-standards-2.7.4
- fe0235a - Merge remote-tracking branch 'origin/master'
- 39ed9dd - A better configuration to copy on release.
- 77d434f - [maven-release-plugin] prepare for next development iteration

### 2.7.3
- a83aa7f - Add CHANGELOG for 2.7.3
- 88631f7 - [maven-release-plugin] prepare release oss-maven-standards-2.7.3
- 5733c99 - Deploy to lime at end of release build as OSS release can take hours to propagate.
- d337aeb - [maven-release-plugin] rollback changes from release preparation of my-branch
- cca5c7c - [maven-release-plugin] prepare branch my-branch
- d605f16 - [maven-release-plugin] prepare for next development iteration

### 2.7.2
- dfb2881 - Add CHANGELOG for 2.7.2
- 41b7563 - [maven-release-plugin] prepare release oss-maven-standards-2.7.2
- a03700b - Deploy at end of build.
- 8e28cc8 - [maven-release-plugin] prepare for next development iteration

### 2.7.1
- f20ba84 - Add CHANGELOG for 2.7.1
- 8239ac6 - [maven-release-plugin] prepare release oss-maven-standards-2.7.1
- 534d255 - Merge remote-tracking branch 'origin/master'
- b320f22 - Enabled failsafe by default in appropriate phase.
- cc3d80e - [maven-release-plugin] prepare for next development iteration

### 2.7.0
- ae8a95c - Add CHANGELOG for 2.7.0
- a636ee4 - [maven-release-plugin] prepare release oss-maven-standards-2.7.0
- 50a7f97 - Updated compiler to use release over source and target.
- 5b13e29 - Rearrange plugin management so that plugins are fetched even when release profile is off.  Refactor libraries to match closed source.
- 2eb11e3 - [maven-release-plugin] prepare for next development iteration

### 2.6.1
- 9b00550 - Add CHANGELOG for 2.6.1
- 72eef12 - [maven-release-plugin] prepare release oss-maven-standards-2.6.1
- 35016fe - Cleanup release profile.
- 4a720bc - [maven-release-plugin] prepare for next development iteration

### 2.6.0
- 40c2d35 - Add CHANGELOG for 2.6.0
- 9123e71 - [maven-release-plugin] prepare release oss-maven-standards-2.6.0
- 8aa7751 - Updated to support boot applications being open sourced.  Library updates for security vulnerabilities.  Changed release to enable the release profile - moved source and doc gen into the release profile.
- 948eec1 - [maven-release-plugin] prepare for next development iteration

### 2.5.1
- dd3631d - Add CHANGELOG for 2.5.1
- 4826944 - [maven-release-plugin] prepare release oss-maven-standards-2.5.1
- c7b571d - Lombok delombok to fire just before javadoc to avoid Intellij issues.  Confirmed javadoc jar generation.
- fe4b317 - Fallback to source directory if lombok not set.
- 33be931 - [maven-release-plugin] prepare for next development iteration

### 2.5.0
- a4e11ed - Add CHANGELOG for 2.5.0
- c329a96 - [maven-release-plugin] prepare release oss-maven-standards-2.5.0
- e09818c - Lombok to annotation processor, default dependencies.  Use spring starter logging as logging dance setup.
- e688143 - Added delombok so the javadoc plugin stops complaining.
- fb0a879 - Added a dev-build profile so we can skip signing.
- 08d6ef7 - Update versions of libraries, spring boot, cloud and awspring to latest.
- d146bcb - [maven-release-plugin] prepare for next development iteration

### 2.0.0
- eb1e713 - Add CHANGELOG for 2.0.0
- 04ce724 - [maven-release-plugin] prepare release oss-maven-standards-2.0.0
- 7a54741 - Remove no fork.
- 5d9b8ca - Relax processig for lombok.
- 48aaabf - Update to version 2 to handle newer spring boot, java 11, etc.
- e914256 - [maven-release-plugin] prepare for next development iteration

### 1.0.1
- 57d2d22 - Add CHANGELOG for 1.0.1
- 7725e04 - [maven-release-plugin] prepare release oss-maven-standards-1.0.1
- ee75653 - GW-5: Remove use of scm.url as its a maven property.
- 86bac19 - GW-5: Realized javadoc is not executing.  Update plugin definition and versions for javadoc and source.
- 4000f6b - [maven-release-plugin] prepare for next development iteration

### 1.0.0
- 8e41114 - Add CHANGELOG for 1.0.0
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
