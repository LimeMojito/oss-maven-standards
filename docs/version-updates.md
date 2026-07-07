# Version Updates

* The plugin update requires manual checks as it is a report.
* Version updates automatic and are configured to skip alpha, beta, rc and old date format versions.
* maven-versions-plugin has backup poms disabled as VCS is here.

## Set a new release version
We use the [Multi Module Maven Release Plugin](https://danielflower.github.io/multi-module-maven-release-plugin).  This adds the build number to the "patch" level in Major.minor.patch-SNAPSHOT.  When releasing, the build will update to M.m.p automatically __for only those modules affected__. 

Version numbers in git represent __business__ semantic version  - Major is incremented for breaking API changes, minor is incremented for new api features.  Tags are available in the repository on a per module basis for release versions including build number.

## Report on what plugin updates are available

```shell
   mvn versions:display-plugin-updates | more

```

## Update all library versions and parent dependencies

```shell
mvn versions:update-parent -U
mvn versions:update-properties -U
mvn versions:use-latest-releases -U
```

## Github Workflow

Github workflows now automatically run the version updates as part of the default branch build.

See Article: https://limemojito.com/version-dependency-updates-automated-in-maven/
