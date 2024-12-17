#!/bin/sh

while read -r local_ref local_sha remote_ref remote_sha; do
  echo "$local_ref $local_sha $remote_ref $remote_sha" | talisman --githook pre-push
done
