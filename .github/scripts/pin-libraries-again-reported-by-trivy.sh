#!/bin/bash

# We start with 
# 1. The list of CVEs that caused pinning
# 2. The list of CVEs that trivy reported after unpinning
# 3. The list of pinned libraries
#
# Then we 
# 1. Compile a file with both, CVE and pinned library information
# 2. Figure out what CVEs no longer need pinnig (i.e. are not reported by trivy if unpinned)
# 3. Unpin these.

LIST_OF_CVES_FILE="cve-list.txt" # expected to exist
LIST_OF_PINNED_FILE="pinned-deps.txt" # expected to exist
LIST_OF_TRIVY_CVES_FILE="trivy-cves.txt" # expected to exist
LIST_OF_CVES_WITH_LIBRARIES="cves-with-libraries.txt" # will be created during operation

echo "Compile CVEs with libraries (for lookup)"
paste $LIST_OF_CVES_FILE $LIST_OF_PINNED_FILE > $LIST_OF_CVES_WITH_LIBRARIES


echo "Walk through CVEs with libraries and check if still needed (= reported by trivy)"
while read -r line; do
    CVE=`echo $line | cut -d' ' -f2`
    LIBRARY_DOMAIN=`echo $line | cut -d'"' -f2`
    LIBRARY_NAME=`echo $line | cut -d'"' -f4`
    echo $CVE - $LIBRARY_DOMAIN - $LIBRARY_NAME

    if grep -q "$CVE" $LIST_OF_TRIVY_CVES_FILE; then
        echo "--> still found by trivy"
        # remove the comments

    else
        echo "--> no longer found by trivy. No more pinning needed."
    fi

done < $LIST_OF_CVES_WITH_LIBRARIES