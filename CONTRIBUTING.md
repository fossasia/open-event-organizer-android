# Contributing

When contributing to this repository, please first discuss the change you wish to make via issue,
email, or any other method with the maintainers of this repository before making a change. 

Please note we have a [Code Of Conduct](https://github.com/fossasia/open-event-orga-app/blob/development/CODE_OF_CONDUCT.md), please follow it in all your interactions with the project.

## Issues Process

1. First step while searching for issues is to check the ones with `core` and `high-priority` labels.
2. Then `unimplemented` issues should be checked which got closed due to lack of attention but still need to be implemented.
3. Then `deferred` issues may be valid now to take up.
4. If no such criteria meets, create new issues using one of these:

[![Bug](https://img.shields.io/badge/issues-bug-red.svg)](https://github.com/fossasia/open-event-orga-app/issues/new?template=bug.md)
[![Feature](https://img.shields.io/badge/issues-feature-green.svg)](https://github.com/fossasia/open-event-orga-app/issues/new?template=feature.md)
[![Chore](https://img.shields.io/badge/issues-chore-blue.svg)](https://github.com/fossasia/open-event-orga-app/issues/new?template=chore.md)

## Pull Request Process

1. Ensure that the tests are passing locally by running ./gradlew build and there are no checkstyle or PMD issues
2. Check that there are no conflicts and your Pull Request passes the Travis build.In case the Travis build fails, checkout the generated logs.
3. Give the description of the issue that you want to resolve in the pull request message. The format of the commit message to be fixed    - Fixes #[issue number] [Description of the issue]
4. Wait for the maintainers to review your pull request and do the changes if requested.

## Contributions Best Practices

**Commits**
* Write clear meaningful git commit messages (Do read http://chris.beams.io/posts/git-commit/)
* Make sure your PR's description contains GitHub's special keyword references that automatically close the related issue when the PR is merged. (More info at https://github.com/blog/1506-closing-issues-via-pull-requests )
* When you make very very minor changes to a PR of yours (like for example fixing a failing travis build or some small style corrections or minor changes requested by reviewers) make sure you squash your commits afterwards so that you don't have an absurd number of commits for a very small fix. (Learn how to squash at https://davidwalsh.name/squash-commits-git )
* When you're submitting a PR for a UI-related issue, it would be really awesome if you add a screenshot of your change or a link to a deployment where it can be tested out along with your PR. It makes it very easy for the reviewers and you'll also get reviews quicker.

**Feature Requests and Bug Reports**
* When you file a feature request or when you are submitting a bug report to the [issue tracker](https://github.com/fossasia/phimpme-android/issues), make sure you add steps to reproduce it. Especially if that bug is some weird/rare one.

**Join the development**
* Before you join development, please set up the project on your local machine, run it and go through the application completely. Press on any button you can find and see where it leads to. Explore. (Don't worry ... Nothing will happen to the app or to you due to the exploring :wink: Only thing that will happen is, you'll be more familiar with what is where and might even get some cool ideas on how to improve various aspects of the app.)
* If you would like to work on an issue, drop in a comment at the issue. If it is already assigned to someone, but there is no sign of any work being done, please free to drop in a comment so that the issue can be assigned to you if the previous assignee has dropped it entirely.

Do read the [Open Source Developer Guide and Best Practices at FOSSASIA](https://blog.fossasia.org/open-source-developer-guide-and-best-practices-at-fossasia).

[homepage]: https://github.com/fossasia/open-event-orga-app
