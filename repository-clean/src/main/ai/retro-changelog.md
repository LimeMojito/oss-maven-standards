Get all the tags for this git repository and sort the tags in semantic version format M.m.p where M is major version, m
is minor version then p is patch version, ordering from the earliest tag to the latest.

For each tag:

- checkout the repository
- summarize the changes from the last tag to this tag into a- CHANGELOG.md file with a heading for this version buy
  examining the source code and summarizing the source changes between the tags. This summary should consist of a
  markdown formatted list that is easy to read.
- Remove all summary items that contain [maven-release-plugin], [Release], [Development] and reference a merge branch.
- Where there are multiple items referring to "version update" replace with a single item the describes the version update.
- Add this updated CHANGELOG.md file to the source represented by the tag.
