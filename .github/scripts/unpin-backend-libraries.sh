#!/bin/bash

# When finding CVEs in the backend code, we pin the fixed versions in "build.gradle.kts" and mark them by "# CVE..."
# Over time some of these pinned dependencies may no longer be needed as their parents contain the fixes for the CVEs already
# This script unpins backend dependencies and writes the CVEs to a file.
# (Other scripts then perform the security scans and the actual unpinning if needed.)
#
# The unpinning is done as follows
#
# 1. Scan the file "build.gradle.kts" for lines that start with "# CVE"
# 2. Write all found CVEs to a file "pinned-dependencies.txt"
# 3. Unpin the pinned CVEs in "build.gradle.kts"

LIST_OF_PINNED_FILE="pinned_deps.txt"
BUILD_GRADLE_KTS="../../backend/build.gradle.kts"

echo "Extracting pinned dependencies to $LIST_OF_PINNED_FILE..."
grep -A1 "// CVE-" $BUILD_GRADLE_KTS | grep -v "^--$" > $LIST_OF_PINNED_FILE || true

echo "Unpinning the pinned dependencies"
while read -r line; do
    echo $line
done < $LIST_OF_PINNED_FILE