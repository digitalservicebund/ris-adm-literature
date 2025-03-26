#!/bin/bash

# We start with 
# 1. The list of CVEs that caused pinning
# 2. The list of CVEs that trivy reported after unpinning
#
# Then we figure out what CVEs no longer need pinnig (i.e. part of 1. but not of 2.)
# and unpin these.

LIST_OF_CVES_FILE="cve-list.txt"
LIST_OF_TRIVY_CVES_FILE="trivy-cves.txt"
LIST_OF_CVES_

echo "Walk through CVEs we started with and check if still needed"
touch 
while read -r line; do
    CVE=`echo $line | cut -d' ' -f2`
    
    if grep -q "$CVE" $LIST_OF_TRIVY_CVES_FILE; then
        echo "$CVE still found by trivy"
        # remove the comments

    else
        echo "$CVE no longer found by trivy. No more pinning needed."
    fi

done < $LIST_OF_CVES_FILE