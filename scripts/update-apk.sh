#!/bin/sh
set -e

git config --global user.name "Travis CI"
git config --global user.email "noreply+travis@fossasia.org"

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/open-event-orga-app" ]; then
    echo "No push. Exiting."
    exit 0
fi

git clone --quiet --branch=apk https://the-dagger:$GITHUB_API_KEY@github.com/fossasia/open-event-orga-app  apk > /dev/null
cp platforms/android/build/outputs/apk/android-debug.apk apk/test-android-debug.apk
cd apk
git add test-android-debug.apk
git commit -m "[Auto] Update Test Apk"
git push origin apk > /dev/null
