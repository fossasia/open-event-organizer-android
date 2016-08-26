mkdir $HOME/daily/
cp -R /home/travis/build/fossasia/open-event-orga-app/platforms/android/build/outputs/apk/android-debug.apk $HOME/daily/
# go to home and setup git
cd $HOME
  git config --global user.email "harshithdwivedi@gmail.com"
  git config --global user.name "the-dagger"

git clone --quiet --branch=gh-pages https://the-dagger:$GITHUB_API_KEY@github.com/fossasia/open-event-orga-app  gh-pages > /dev/null
cd gh-pages
cp -Rf $HOME/daily/*  sample-apk/
git add -f .
  git add -f .
  git commit -m "Update Sample Apk [skip ci]"
  git push origin gh-pages > /dev/null
