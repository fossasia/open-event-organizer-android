![Open Event Organizer](https://storage.googleapis.com/eventyay.com/assets/branding/organizer_app_branding.png)


## Open Event Organizer App

[![Build Status](https://img.shields.io/travis/fossasia/open-event-orga-app/master.svg?style=flat-square)](https://travis-ci.org/fossasia/open-event-orga-app)
[![Codacy Grade](https://img.shields.io/codacy/grade/e27821fb6289410b8f58338c7e0bc686.svg?style=flat-square)](https://www.codacy.com/app/mb/open-event-orga-app)
[![Gitter](https://img.shields.io/badge/chat-on%20gitter-ff006f.svg?style=flat-square)](https://gitter.im/fossasia/open-event-orga-app)

The core features of this Android Application are
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

### Libraries:
- [RxJava 2](https://github.com/ReactiveX/RxJava)
- [Dagger 2](https://github.com/google/dagger)
- [ButterKnife](https://github.com/JakeWharton/butterknife)
- [Lombok](https://projectlombok.org/)
- [Picasso](https://github.com/square/picasso)
- [Retrofit](https://github.com/square/retrofit) + [Okhttp](https://github.com/square/okhttp)
- [DBFlow](https://github.com/Raizlabs/DBFlow)
- [FastAdapter](https://github.com/mikepenz/FastAdapter)
- [Leakcanary](https://github.com/square/leakcanary)
- [Sentry](https://github.com/getsentry/sentry-java)
- [Timber](https://github.com/JakeWharton/timber)
- Testing:
  - [JUnit4](https://github.com/junit-team/junit4)
  - [Mockito](https://github.com/mockito/mockito)

### Development setup
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

### Project Conventions

There is certain conventions we follow in the project, we recommend that you become familiar with these so that the development process is uniform for everyone:

#### Dependency Injection

We use Dagger 2 for DI, so please take a look at how it works. We did not create very complex graphs, component or scopes to keep it simple and easy to refactor. But, we do have certain guidelines as to what needs to be injected and how. Every object which is configurable or there is a possibility for it to be shared among objects, instances or lifecycles in future, must be injected through DI. The interface implementations which have obvious constructions are `@Bind`ed to their concrete classes and a general rule of thumb we follow is to have only one `new` keyword in the injectable construction (the `@Provides` method in Dagger). Meaning that all other dependencies that need to be instantiated during its creation must be passed as arguments and provided by the DI itself.

#### MVP

The project follows Model-View-Presenter design pattern and requires schematic interfaces for each component to be written first as contracts and then implemented.   
All the interactions are done using interfaces only and concrete classes are only used when being injected into required positions. This means any model, view or presenter will only be referenced by its interface. We do so it is easy to mock and test them and there is no discrepancy in the callable methods of the concrete class and the interface.  
We realize that MVP is opinionated and there is no strict boundary between the responsibility of each component, but we recommend following this style:
- `View` is passive and dumb, there is no logic to be exercised in View, only the ability to show data provided by the presenter through contract is present in the View. This makes it easy to unit test and remove the dependence on Android APIs, thus making the need of instrumentation tests scarce
- `Presenter` is responsible for most of the business logic, manipulation of data and organising it for the view to present. All logic for the app is present here and it is devoid of ANY Android related code, making it 100% unit testable. We have created wrapper around common Android APIs in form of models so that they can be mocked and presenter stays clean. The responsibility of presenter includes the fetching of data from external source, observing changes and providing the view with the latest data. It also needs to handle all View interactions that require any logic, such as UI triggers causing complex interactions. Notable exception for this is launching of an Activity on click of a button. There is no logic required in the action and is completely dependent on Android APIs. Lastly, presenter should always clean up after the view is detached to prevent memory leaks
- `Model` has the responsibility to hold the data, load it intelligently from appropriate source, be it disk or network, monitor the changes and notify presenter about those, be self sufficient; meaning to update data accordingly as needed without any external trigger (saving the data in disk after updating from network and providing the saved data from next time on), but be configurable (presenter may be able to ask for fresh data from network). The presenter should not worry about the data loading and syncing conditions (like network connectivity, failed update, scheduling jobs, etc) as it is the job of model itself.

#### Project Structure

Generally, projects are created using package by layer approach where packages are names by layers like `ui`, `activity`, `fragment`, etc but it quickly becomes unscalable in large projects where large number of unrelated classes are crammed in one layer and it becomes difficult to navigate through them.  
Instead, we follow package by feature, which at the cost of flatness of our project, provides us packages of isolated functioning related classes which are likely to be a complete self sufficient component of the application. Each package all related classes of view, presenter, their implementations like Activities anf Fragments.  
A notable exception to this is the `common` module and data classes like Models and Repositories as they are used in a cross component way.  
***Note:** The interface contract for Presenter and View is present in `contract` package in each module`*

#### Unit Testing

We have tight and almost complete coverage of unit tests for models and presenters and it was possible because we have focused on adding conditional unit tests with each functionality we have added. Each functionality was tested under various conditions making the tests self documenting about the functionality of app and saved us from various regressions that are caused after rapid development and refactoring of the application. Because we require the developer to write unit tests along with the code, we build up the confidence and credibility of the code base and remove the lag between functionality and test, making it hard for bugs to creep in between that period. Furthermore, if we let PRs merge without addition of unit tests, and the author of PR does not choose to write tests for it, it becomes difficult for someone else to just write tests for someone else's code and brings the coverage down and may cause regressions in future. We and everyone else wants to focus on creating the app better than to keep solving bugs and writing tests as we write code is the only solution.  
So, please take a look at already written tests(they are fairly self-documenting) and try to write tests for each functionality you add.

#### Separation of concerns

Lastly, each class should only perform one task, do it well, and be unit tested for it. For example, if a presenter is doing more than it should, i.e., parsing dates or implementing search logic, better move it in its own class. There can be exceptions for this practice, but if the functionality can be generalised and reused, it should most definitely be transferred in its own class and unit tested.

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

