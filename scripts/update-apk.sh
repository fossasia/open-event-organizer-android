#!/bin/sh
set -e

git config --global user.name "Travis CI"
git config --global user.email "noreply+travis@fossasia.org"

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/open-event-orga-app" ]; then
    echo "No push. Exiting."
    exit 0
fi

git clone --quiet --branch=gh-pages https://the-dagger:$GITHUB_API_KEY@github.com/fossasia/open-event-orga-app  gh-pages > /dev/null
cp platforms/android/build/outputs/apk/android-debug.apk gh-pages/sample-apk/android-debug.apk
cd gh-pages
git add sample-apk/android-debug.apk
git commit -m "[Auto] Update Sample Apk"
git push origin gh-pages > /dev/null
