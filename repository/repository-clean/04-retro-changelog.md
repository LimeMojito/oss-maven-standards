The aim of this task is to have a CHANGELOG.md file at each tag in the repository.

"Semantic Version" is a version number system with the following format: M.m.p where M is a number representing the major version, m is a number representing the minor version, and p is a number representing the patch version. Sorting of semantic version numbers is done by comparing the numbers in each component from left to right.
                                                                                   
Get all the tags for this git repository and sort the tags in semantic version format sorted in ascending order.

For each tag:
  - checkout the tag
  - summarize the changes from the previous tag to this tag into a CHANGELOG.md file with a heading for this tag by examining the source code and summarizing the source changes between the tags. This summary should consist of a markdown formatted list that is easy to read.  The version headings should be in descending order of semantic version.
    - Where there are multiple items referring to "version update" or "Update versions" then replace them with a single item that describes the version update.
    - Remove all summary items that contain 
      - [maven-release-plugin]
      - [Release]
      - [Development]
      - [artifactory-release]
      - merge branch
      - merge remote-tracking branch
      - updating poms
      - Update versions
  - Add the CHANGELOG.md file to the git repository at the tag performing git local operations to achieve this outcome.
             
Validate that each tag when checked out has had a CHANGELOG.md file generated or updated.
