A release branch is a branch that contains the string "release" in its ref.

If there are no release branches in the repository, stop this task.

For each release branch in the repository;
- create a tag with the same name that represents the latest default branch merge of that release branch if that tag does not exist in the repository. 
