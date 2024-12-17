#!/bin/sh
cat < ../allowed-licenses.json | jq -r '.allowedLicenses | map(.moduleLicense) | join(";")'