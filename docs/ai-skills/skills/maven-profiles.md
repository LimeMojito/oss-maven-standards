# Skill: Use Maven Profiles to adjust build capabilities

These profiles add capabilities to our builds or allow "quick checks" as wanted by the developer.

For example, `mvn -Pfast-build -T6 install` to perform a fast build with threads for speed.

For example, `mvn -Pincremental install` to perform a faster build on a monorepo where the feature branch differs from main.

| Profile     | Actions                                                                                                |
|-------------|--------------------------------------------------------------------------------------------------------|
|             | No profile performs a full build with all tests and checks.  Not thread safe.                          |
| fast-build  | Quickly build all the deliverables.  No deployments, checks, tests, etc. Can be performed with threads |
| incremental | Build only those moduiles changed from the default branch. Not thread safe.                            |
| release     | Perform a release build with all checks enabled and deployments.  Not thread safe.                     |
