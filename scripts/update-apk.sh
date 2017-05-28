#!/bin/sh
set -e

git config --global user.name "Travis CI"
git config --global user.email "noreply+travis@fossasia.org"

export DEPLOY_BRANCH=${DEPLOY_BRANCH:-master}

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/open-event-orga-app" -o  "$TRAVIS_BRANCH" != "$DEPLOY_BRANCH" ]; then
    echo "We upload apk only for changes in master. So, let's skip this shall we ? :)"
    exit 0
fi


git clone --quiet --branch=apk https://niranjan94:$GITHUB_API_KEY@github.com/fossasia/open-event-orga-app apk > /dev/null
cd apk
rm *.apk
cp /home/travis/build/fossasia/open-event-orga-app/app/build/outputs/apk/*.apk .
for file in *; do
  mv $file test-${file%%}
done

# Create a new branch that will contains only latest apk
git checkout --orphan temporary

# Add generated APK
git add --all .
git commit -am "[Auto] Update Test Apk ($(date +%Y-%m-%d.%H:%M:%S))"

# Delete current apk branch
git branch -D apk
# Rename current branch to apk
git branch -m apk

# Force push to origin since histories are unrelated
git push origin apk --force --quiet > /dev/null
