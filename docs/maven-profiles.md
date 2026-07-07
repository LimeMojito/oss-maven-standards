# Maven Profiles

These profiles add capabilities to our builds or allow "quick checks" as wanted by the developer.

| Profile     | Actions                                                                         |
|-------------|---------------------------------------------------------------------------------|
|             | Perform a "merge" build.  Checks and installs suitable for a release candidate. |
| fast-build  | Quickly build all the deliverables.  No deployments, checks, tests, etc.        |
| incremental | Build only those moduiles changed from the default branch.                      |
| release     | Perform a release build with all checks enabled and deployments.                |
