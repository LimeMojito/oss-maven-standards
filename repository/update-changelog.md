Using the version number from pom.xml minus -SNAPSHOT, update CHANGELOG.md to summarize the git commits since the last release tag,
where the last release tag is the top most heading of changes in the current CHANGELOG.md.

In the summary that has been added perform the following;
- where there are multiple summary items referring to version updates or pom updates, replace them with a single item that is "Updated versions and security patches."
- remove all summary items that contain any of the following
  - [maven-release-plugin]
  - [Release]
  - [Development]
  - [artifactory-release]
  - updating poms
- Remove any summary items that refer to merges.

Add a heading of Latest Changes with content as a link to the CHANGELOG.md in the Readme.md file if it doesn't already exist.
