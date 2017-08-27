## Android Development setup

Before you begin, you should already have the Android Studio SDK downloaded and set up correctly. You can find a guide on how to do this here: [Setting up Android Studio](http://developer.android.com/sdk/installing/index.html?pkg=studio).

1. Download the project [source](https://github.com/fossasia/open-event-orga-app). You can do this either by forking and cloning the repository (recommended if you plan on pushing changes) or by downloading it as a ZIP file and extracting it.

2. Open Android Studio, you will see a **Welcome to Android** window. Under Quick Start, select _Import Project (Eclipse ADT, Gradle, etc.)_

3. Navigate to the directory where you saved the project, select the root folder of the project (the folder named "open-event-orga-app"), and hit OK. Android Studio should now begin building the project with Gradle.

4. Once this process is complete and Android Studio opens, check the Console for any build errors.

  - _Note:_ If you receive a Gradle sync error titled, "failed to find ...", you should click on the link below the error message (if avaliable) that says _Install missing platform(s) and sync project_ and allow Android studio to fetch you what is missing.

5. Once all build errors have been resolved, you should be all set to build the app and test it.

6. To Build the app, go to _Build>Make Project_ (or alternatively press the Make Project icon in the toolbar).

7. If the app was built successfully, you can test it by running it on either a real device or an emulated one by going to _Run>Run 'app'_ or presing the Run icon in the toolbar.

**Note: You will need to install Lombok Plugin in Android Studio in order to properly access generated methods of data classes**

**Note:** For release builds, you need to set up Sentry with DSN in environment variables and a sentry.properties file in project root containing these properties in order for sentry cli to automatically upload proguard mappings to your project:
- defaults.project
- defaults.org
- auth.token

Also, you need to enable the option by changing `autoUpload false` to true in order to enable this feature

For more info, visit https://docs.sentry.io/clients/java/modules/android/

You can alternatively disable proguard altogether

### Configuring App Name (Optional)

Default app name is *eventyay organizer*. This can be changed by setting environment variable `app_name` which will replace the default app name.