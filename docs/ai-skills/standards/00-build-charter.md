# Build Charter

1. The build enforces our development standards to reduce the code review load.
2. The build must have a simple developer interface – ```mvn clean install```.
3. If the clean install passes – we can move to source Pull Request (PR).
4. PR is important, as when a PR is merged we may automatically deploy to production.
5. Creating a new project or module must not require a lot of configuration (“xml hell”).
6. A module must not depend on another running Lime Mojito module for testing.
7. Any stub resources for testing must be a docker image.
    * Postgres, Localstack, etc
8. Stubs will be managed by the build process for integration test phase.
    * ie if you’re using stubs, you’re building a failsafe maven plugin IT test case.
9. The build will handle style and code metric checks (CheckStyle, Maven Enforcer, etc) so that we do not waste time in
   PR reviews.
10. For open source, we will post to Maven Central on a Release Build.
