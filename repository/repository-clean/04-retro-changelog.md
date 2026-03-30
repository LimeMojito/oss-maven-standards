Get all the tags for this git repository and sort the tags in semantic version format M.m.p where M is major version, m
is minor version then p is patch version.

For each tag sorted in semantic version order - M then m then p compared numerically in ascending order
  - summarize the changes from the last tag to this tag into a- CHANGELOG.md file with a heading for this version by
    examining the source code and summarizing the source changes between the tags. This summary should consist of a markdown formatted list that is easy to read.
    - Where there are multiple items referring to "version update" or "Update versions" replace with a single item the describes the version update.
    - Remove all summary items that contain 
      - [maven-release-plugin]
      - [Release]
      - [Development]
      - [artifactory-release]
      - merge branch
      - merge remote-tracking branch
      - updating poms
      - Update versions
    - Add this updated CHANGELOG.md file to the source represented by the tag.
