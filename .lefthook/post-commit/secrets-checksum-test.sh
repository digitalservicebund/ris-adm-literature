#!/bin/sh
local_reference=$(git rev-parse --symbolic-full-name HEAD)
local_object_name=$(git rev-parse HEAD)
# Strong assumption that we run after the commit has been created (not before)!
remote_reference=$(git branch --format="%(upstream)" --list $(git rev-parse --abbrev-ref HEAD))

remote_object_name=$(git rev-parse $remote_reference)

echo $local_reference \
  $local_object_name \
  ${remote_reference:-0000000000000000000000000000000000000000} \
  ${remote_object_name:-0000000000000000000000000000000000000000} \
  | talisman --githook pre-push
