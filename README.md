# open-event-orga-app

[![Build Status](https://travis-ci.org/fossasia/open-event-orga-app.svg?branch=master)](https://travis-ci.org/fossasia/open-event-orga-app)
[![Join the chat at https://gitter.im/fossasia/open-event-orga-app](https://badges.gitter.im/fossasia/open-event-orga-app.svg)](https://gitter.im/fossasia/open-event-orga-app?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Mobile App for Organizers and Entry Manager

The main feature of the app is: Scan a QR code, check in attendees and keep it inline with the orga server data.

## Roadmap

Future features are to give overviews of sales, tracks and sessions, but all this can come later.

## Communication

Please join our mailing list to discuss questions regarding the project: https://groups.google.com/forum/#!forum/open-event

Our chat channel is on gitter here: https://gitter.im/fossasia/open-event-orga-app

## Development

This project uses [Ionic Framework v1.x](http://ionicframework.com/).

### Development environment setup
- Install [Node.js](https://nodejs.org/en/). (v4.x.x recommended).
- Install ionic framework CLI tool, apache cordova and gulp globally
```
npm install -g ionic cordova gulp
```
- Install nodejs-legacy
```
apt install nodejs-legacy
```
- Clone this repository
```
git clone https://github.com/fossasia/open-event-orga-app.git
```
- From with the cloned repository, install all the Node.js dependencies using `npm`.
```
npm install
```
- Build javascript assets.
```
gulp
```
Depending on the platform you wish to build for (i.e Android or iOS) you will have to setup the required build tools and SDKs. 

#### Building for Android
- Install [Java Development Kit (JDK) 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or later.
- Install the [Android SDK Tools or Android Studio](https://developer.android.com/studio/index.html#downloads).
- Install required Android SDK Packages from the SDK Manager
	- Android Platform SDK for API 24
	- Android SDK Build tools 24.x.x
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

* [Node.js](https://nodejs.org/en/)
* [Ionic Framework v1](http://ionicframework.com/)
* [AngularJS 1](https://angularjs.org/)
* [Apache Cordova](https://cordova.apache.org/)
* [Browserify](http://browserify.org/)
* [Gulp](http://gulpjs.com/)
