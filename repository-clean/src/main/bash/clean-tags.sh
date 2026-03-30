#!/bin/bash
#
# Copyright 2011-2026 Lime Mojito Pty Ltd
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#
#

# Iterate through git tags, find matching version, rename the remote tag to the version name.
# Log the name change to stdout.

# Fetch all remote tags first to be up-to-date
git fetch --tags origin

git tag | while read -r tag; do
  version=$(echo "$tag" | grep -E -o '[0-9]+\.[0-9]+\.[0-9]+' | head -n 1)
  if [[ -n "$version" && "$tag" != "$version" ]]; then
    # Check if the target tag already exists locally
    if git tag -l | grep -q "^$version$"; then
      echo "Skipping rename of $tag to $version as $version already exists"
    else
      echo "Renaming tag $tag to $version"
      git tag "$version" "$tag"
      git push origin "$version"
      git push origin --delete "$tag"
      git tag -d "$tag"
    fi
  else
    echo "Deleting tag $tag (does not match version format)"
    git push origin --delete "$tag"
    git tag -d "$tag"
  fi
done
