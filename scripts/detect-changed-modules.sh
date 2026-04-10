#!/bin/bash
set -e

# Detect if we are in a CI environment
if [ -n "$GITHUB_EVENT_PATH" ]; then
    EVENT_NAME=${GITHUB_EVENT_NAME}
    EVENT_PATH=${GITHUB_EVENT_PATH}
else
    # For local testing, allow passing event name and path as arguments
    EVENT_NAME=$1
    EVENT_PATH=$2
fi

echo "Detecting changed modules for event: $EVENT_NAME"

BASE_SHA=""
HEAD_SHA=""

if [ "$EVENT_NAME" == "pull_request" ]; then
    BASE_SHA=$(jq -r .pull_request.base.sha "$EVENT_PATH")
    HEAD_SHA=$(jq -r .pull_request.head.sha "$EVENT_PATH")
elif [ "$EVENT_NAME" == "push" ]; then
    BASE_SHA=$(jq -r .before "$EVENT_PATH")
    HEAD_SHA=$(jq -r .after "$EVENT_PATH")
    # Handle first push to a branch
    if [[ "$BASE_SHA" == "0000000000000000000000000000000000000000" ]]; then
        BASE_SHA=$(git rev-parse HEAD~1 2>/dev/null || echo "")
    fi
fi

echo "Diff range: $BASE_SHA .. $HEAD_SHA"

PL_FLAGS=""

if [ -n "$BASE_SHA" ] && [ -n "$HEAD_SHA" ] && [ "$BASE_SHA" != "$HEAD_SHA" ]; then
    # Get changed files
    CHANGED_FILES=$(git diff --name-only "$BASE_SHA" "$HEAD_SHA" || echo "")
    
    if [ -z "$CHANGED_FILES" ]; then
        echo "No changed files detected."
    else
        echo "Changed files:"
        echo "$CHANGED_FILES"
        
        # Get modules from pom.xml (root)
        # We look for <module>...</module> entries
        MODULES=$(sed -n 's/.*<module>\(.*\)<\/module>.*/\1/p' pom.xml | sort -u)
        echo "Available modules: $(echo $MODULES | tr '\n' ' ')"
        
        # Regex for files that trigger a full build
        # 1. Root pom.xml
        # 2. .github directory
        # 3. scripts directory (this script and others)
        # 4. Any other file in the root directory (excluding dotfiles maybe, but let's be safe)
        GLOBAL_FILES_REGEX="^(pom\.xml|\.github/|scripts/|\.gitignore|LICENSE|CHANGELOG|Readme|[^/]+$)"
        
        FULL_BUILD=false
        CHANGED_MODULES=""
        
        while IFS= read -r file; do
            if [ -z "$file" ]; then continue; fi
            if [[ $file =~ $GLOBAL_FILES_REGEX ]]; then
                echo "Global file change detected: $file. Forcing full build."
                FULL_BUILD=true
                break
            fi
            
            # Identify which module the file belongs to
            # We take the first part of the path
            FILE_MODULE=$(echo "$file" | cut -d'/' -f1)
            
            if echo "$MODULES" | grep -qw "$FILE_MODULE"; then
                if [[ ! ",$CHANGED_MODULES," =~ ",$FILE_MODULE," ]]; then
                    if [ -n "$CHANGED_MODULES" ]; then
                        CHANGED_MODULES="$CHANGED_MODULES,$FILE_MODULE"
                    else
                        CHANGED_MODULES="$FILE_MODULE"
                    fi
                fi
            else
                echo "File $file is not in a known module and not a global file. Forcing full build."
                FULL_BUILD=true
                break
            fi
        done <<< "$CHANGED_FILES"
        
        if [ "$FULL_BUILD" = false ] && [ -n "$CHANGED_MODULES" ]; then
            echo "Building modules: $CHANGED_MODULES"
            PL_FLAGS="-pl $CHANGED_MODULES -am -amd"
        else
            echo "Full build required."
        fi
    fi
else
    echo "Could not determine diff range or same SHA. Defaulting to full build."
fi

# Set output for GitHub Actions
if [ -n "$GITHUB_OUTPUT" ]; then
    echo "pl_flags=$PL_FLAGS" >> "$GITHUB_OUTPUT"
fi

echo "Final pl_flags: $PL_FLAGS"
