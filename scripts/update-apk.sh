#!/bin/sh
set -e

git config --global user.name "Travis CI"
git config --global user.email "noreply+travis@fossasia.org"

export DEPLOY_BRANCH=${DEPLOY_BRANCH:-master}

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/open-event-orga-app" -o  "$TRAVIS_BRANCH" != "$DEPLOY_BRANCH" ]; then
    echo "We upload apk only for changes in master. So, let's skip this shall we ? :)"
    exit 0
fi

git clone --quiet --branch=apk https://the-dagger:$GITHUB_API_KEY@github.com/fossasia/open-event-orga-app apk > /dev/null
cp platforms/android/build/outputs/apk/android-debug.apk apk/test-android-debug.apk
cd apk
git checkout --orphan temporary
git add test-android-debug.apk
git commit -am "[Auto] Update Test Apk ($(date +%Y-%m-%d.%H:%M:%S))"
git branch -D apk
git branch -m apk
git push origin apk --force --quiet > /dev/null
