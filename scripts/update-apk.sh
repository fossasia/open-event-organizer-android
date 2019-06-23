#!/bin/sh
set -e

git config --global user.name "Travis CI"
git config --global user.email "noreply+travis@fossasia.org"

export DEPLOY_BRANCH=${DEPLOY_BRANCH:-development}
export PUBLISH_BRANCH=${PUBLISH_BRANCH:-master}

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/open-event-orga-app" ] || ! [ "$TRAVIS_BRANCH" == "$DEPLOY_BRANCH" -o "$TRAVIS_BRANCH" == "$PUBLISH_BRANCH" ]; then
    echo "We upload apk only for changes in development or master, and not PRs. So, let's skip this shall we ? :)"
    exit 0
fi

git clone --quiet --branch=apk https://niranjan94:$GITHUB_API_KEY@github.com/fossasia/open-event-orga-app apk > /dev/null
cd apk

if [ "$TRAVIS_BRANCH" == "$PUBLISH_BRANCH" ]; then
	/bin/rm -f *
else
	/bin/rm -f eventyay-organizer-dev-*.apk
fi

\cp -r ../app/build/outputs/apk/playStore/*/**.apk .
\cp -r ../app/build/outputs/apk/fdroid/*/**.apk .
\cp -r ../app/build/outputs/apk/playStore/release/output.json playStore-release-output.json
\cp -r ../app/build/outputs/apk/fdroid/release/output.json fdroid-release-output.json

# Signing Apps

if [ "$TRAVIS_BRANCH" == "$PUBLISH_BRANCH" ]; then
    echo "Push to master branch detected, signing the app..."
    cp app-playStore-release-unsigned.apk app-playStore-release-unaligned.apk
	jarsigner -tsa http://timestamp.comodoca.com/rfc3161 -sigalg SHA1withRSA -digestalg SHA1 -keystore ../scripts/key.jks -storepass $STORE_PASS -keypass $KEY_PASS app-playStore-release-unaligned.apk $ALIAS
	${ANDROID_HOME}/build-tools/${BUILD_TOOLS_VERSION}/zipalign -p 4 app-playStore-release-unaligned.apk app-playStore-release.apk
fi

if [ "$TRAVIS_BRANCH" == "$PUBLISH_BRANCH" ]; then
    for file in app*; do
          cp $file eventyay-organizer-master-${file%%}
    done
fi

if [ "$TRAVIS_BRANCH" == "$DEPLOY_BRANCH" ]; then
    for file in app*; do
          mv $file eventyay-organizer-dev-${file%%}
    done
fi

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

# Publish App to Play Store
if [ "$TRAVIS_BRANCH" != "$PUBLISH_BRANCH" ]; then
    echo "We publish apk only for changes in master branch. So, let's skip this shall we ? :)"
    exit 0
fi

gem install fastlane
fastlane supply --apk eventyay-organizer-master-app-playStore-release.apk --track alpha --json_key ../scripts/fastlane.json --package_name $PACKAGE_NAME
