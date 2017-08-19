#!/bin/sh
set -e

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/open-event-orga-app" ]; then
    echo "We decrypt key only for pushes to the branches and not PRs. So, skip."
    exit 0
fi

openssl aes-256-cbc -K $encrypted_6735e748860e_key -iv $encrypted_6735e748860e_iv -in ./scripts/evenyay-orga-upload.jks.enc -out ./scripts/evenyay-orga-upload.jks -d