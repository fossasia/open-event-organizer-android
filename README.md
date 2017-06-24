![Open Event Frontend](https://storage.googleapis.com/eventyay.com/assets/branding/organizer_app_branding.png)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/48552eea0e5749978f771db8fd854a40)](https://www.codacy.com/app/mejariamol/open-event-orga-app?utm_source=github.com&utm_medium=referral&utm_content=fossasia/open-event-orga-app&utm_campaign=badger)
[![Build Status](https://img.shields.io/travis/fossasia/open-event-orga-app/master.svg?style=flat-square)](https://travis-ci.org/fossasia/open-event-orga-app)
[![Gitter](https://img.shields.io/badge/chat-on%20gitter-ff006f.svg?style=flat-square)](https://gitter.im/fossasia/open-event-orga-app)


> Mobile App for Organizers and Entry Managers.

The core features of this application are
- Scan a QR code
- Check-in attendees 
- Data sync with the [Open Event Organizer Server](https://github.com/fossasia/open-event-orga-server/)

## Roadmap

Planned features & enhancements are:
- Overview of sales
- Overview of tracks and sessions
- Quick session re-scheduling
- Push notifications for certain triggers

## Communication

Please join our mailing list to discuss questions regarding the project: https://groups.google.com/forum/#!forum/open-event

Our chat channel is on gitter here: https://gitter.im/fossasia/open-event-orga-app

## Development


- The [Open Event Organizer Server](https://github.com/fossasia/open-event-orga-server) acts as the backend for this application. The API docs for the same can be accessed at [https://open-event-dev.herokuapp.com/api/v1](https://open-event-dev.herokuapp.com/api/v1/) .
- This project uses [Ionic Framework v2.x](http://ionicframework.com/docs/v2).

### Development environment setup
- Install [Node.js](https://nodejs.org/en/). (v6.x.x recommended).
    - **Linux/OS X Users** - You can use [Node Version Manager](https://github.com/creationix/nvm) to install and manage Node.js versions.
    - **Windows Users** - You can download the lastest installer from [nodejs.org](https://nodejs.org/en/) and install it directly.
- Install ionic framework CLI tool and apache cordova globally
```
npm install -g ionic cordova
```
- Fork this repository this repository into your account
- Clone the forked repository
```
git clone https://github.com/<username>/open-event-orga-app.git
```
- From with the cloned repository, install all the Node.js dependencies using `npm`.
```
npm install
```
Depending on the platform you wish to build for (i.e Android or iOS) you will have to setup the required build tools and SDKs. 

#### Building for Android
- Install [Java Development Kit (JDK) 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or later.
- Install the [Android SDK Tools or Android Studio](https://developer.android.com/studio/index.html#downloads).
- Install required Android SDK Packages from the SDK Manager
	- Android Platform SDK for API 25
	- Android SDK Build tools 25.x.x
	- Android Support Repository
- Setup required environment variables and PATH
	- Set the `JAVA_HOME` environment variable to the location of your JDK installation
	- Set the `ANDROID_HOME` environment variable to the location of your Android SDK installation
	- Add the Android SDK's `tools` and `platform-tools` directories to your `PATH`
- Create android platform specific build files for our Ionic app
```
ionic platform add android
```
- Generate the icon and splash screen resources
```
ionic resources
```
- Build the android application
```
ionic build android
```
- The app will have been built and located at `platforms/android/build/outputs/apk/android-debug.apk`

#### Building for iOS
- Install Xcode from the [App store](https://itunes.apple.com/us/app/xcode/id497799835?mt=12)
- Once Xcode is installed, few command-line tools need to be enabled. From the command line, run:
```
xcode-select --install
```
- Create iOS platform specific build files for our Ionic app
```
ionic platform add ios
```
- Generate the icon and splash screen resources
```
ionic resources
```
- Build the iOS application
```
ionic build ios
```

## Technology Stack

* [Node.js v6.x](https://nodejs.org/en/)
* [Ionic Framework v3.x](http://ionicframework.com/docs/v2/)
* [Angular v4.0.x](https://angular.io/)
* [Apache Cordova v6.5.x](https://cordova.apache.org/)


## Contributions Best Practices

**Commits**
* Write clear meaningful git commit messages (Do read http://chris.beams.io/posts/git-commit/)
* Make sure your PR's description contains GitHub's special keyword references that automatically close the related issue when the PR is merged. (More info at https://github.com/blog/1506-closing-issues-via-pull-requests )
* When you make very very minor changes to a PR of yours (like for example fixing a failing travis build or some small style corrections or minor changes requested by reviewers) make sure you squash your commits afterwards so that you don't have an absurd number of commits for a very small fix. (Learn how to squash at https://davidwalsh.name/squash-commits-git )
* When you're submitting a PR for a UI-related issue, it would be really awesome if you add a screenshot of your change or a link to a deployment where it can be tested out along with your PR. It makes it very easy for the reviewers and you'll also get reviews quicker.

**Feature Requests and Bug Reports**
* When you file a feature request or when you are submitting a bug report to the [issue tracker](https://github.com/fossasia/open-event-orga-app/issues), make sure you add steps to reproduce it. Especially if that bug is some weird/rare one.

**Join the development**
* Before you join development, please set up the project on your local machine, run it and go through the application completely. Press on any button you can find and see where it leads to. Explore. (Don't worry ... Nothing will happen to the app or to you due to the exploring :wink: Only thing that will happen is, you'll be more familiar with what is where and might even get some cool ideas on how to improve various aspects of the app.)
* If you would like to work on an issue, drop in a comment at the issue. If it is already assigned to someone, but there is no sign of any work being done, please free to drop in a comment so that the issue can be assigned to you if the previous assignee has dropped it entirely.

## License

This project is currently licensed under the GNU General Public License v3. A copy of LICENSE.md should be present along with the source code. To obtain the software under a different license, please contact [FOSSASIA](http://blog.fossasia.org/contact/).

