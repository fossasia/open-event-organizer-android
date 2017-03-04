#  Open Event Mobile App for Organizers

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

This project uses [Ionic Framework v2.x](http://ionicframework.com/docs/v2).

### Development environment setup
- Install [Node.js](https://nodejs.org/en/). (v6.x.x recommended).
    - **Linux/OS X Users** - You can use [Node Version Manager](https://github.com/creationix/nvm) to install and manage Node.js versions.
    - **Windows Users** - You can download the lastest installer from [nodejs.org](https://nodejs.org/en/) and install it directly.
- Install ionic framework CLI tool and apache cordova globally
```
npm install -g ionic cordova
```
- Clone this repository
```
git clone https://github.com/fossasia/open-event-orga-app.git
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
	- Android Platform SDK for API 23
	- Android SDK Build tools 23.x.x
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
* [Ionic Framework v2.x](http://ionicframework.com/docs/v2/)
* [Angular v2.2.x](https://angular.io/)
* [Apache Cordova v6.5.x](https://cordova.apache.org/)

## License

This project is currently licensed under the GNU General Public License v3. A
copy of LICENSE.md should be present along with the source code. To obtain the
software under a different license, please contact FOSSASIA.

