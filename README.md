# Open Event Orga App

This is the apk branch of the The Open Event Orga App project, that holds Android apps that get generated on pull requests in the development and master branch and can be used to explore the features of the app and for testing.

More information on the project on the README.md of the development branch.

## Functioning of the APK Branch

Travis does not support Android and docker builds simultaneously, but this is what is required in order to get automatic builds of the Android app.

For every push to dev or master, an apk gets generated along with some meta files (branch, commit hash) and gets pushed to the apk branch.