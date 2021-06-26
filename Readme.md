# Android Client for V Transfer
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) [![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)](https://join.slack.com/t/codegambit/shared_invite/zt-pe1nuhbk-iPuFm2B1JuMS86od4a4wXQ) [![License](https://img.shields.io/badge/License-GPL-lightgrey.svg?style=for-the-badge)](https://github.com/code-gambit/VT-AndroidClient/blob/master/LICENSE) ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/code-gambit/VT-AndroidClient?style=for-the-badge) <br>
[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/code-gambit/VT-AndroidClient/Android%20Build?style=for-the-badge)](https://github.com/code-gambit/VT-AndroidClient/actions/workflows/build.yml) ![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white) [![GitHub last commit](https://img.shields.io/github/last-commit/code-gambit/VT-AndroidClient?style=for-the-badge)](https://github.com/code-gambit/VT-AndroidClient/commits)

[![qr](https://user-images.githubusercontent.com/31315800/123507440-fb98f800-d686-11eb-9cad-7f73dfdcfa22.png)](https://rebrand.ly/VT-APK)<br>
[&#160;&#160;&#160;Download APK&#160;&#160;&#160;](https://rebrand.ly/VT-APK)

## Project description
This project is the android client for V Transfer, allowing people to share their file easily with minimum steps without compromising with the security. At the backend we are using [IPFS](https://ipfs.io/) to upload file which is a blockchain based technology for file sharing making it secure and reliable, below are some of the key feature of this app.
1. Upload file and get a short url for sharing
2. Create different URL with different configuration like
    * Limit click count
    * Control visibility of url (suspending the url temporarily)
    * Deleting the url
3. View file details and related urls
4. User authentication for better security
5. Manage user details
6. Support dark and light mode

## Development Setup
Before setting up the development environment make sure you have downloaded the Android Studio SDK and set it up correctly. You can find a guide on how to do this here: [Setting up Android Studio](http://developer.android.com/sdk/installing/index.html?pkg=studio).

## Building the Code

1. Clone the repository using command: `git clone https://github.com/code-gambit/VT-AndroidClient.git`
2. Open Android Studio.
3. Click on 'Open an existing Android Studio project'
4. Browse to the directory where you cloned the android-client repo and click OK.
5. Let Android Studio import the project.
6. Build the application in your device by clicking run button.

## Contributing
1. Fork it
2. Create your feature branch `(git checkout -b my-new-feature)`
3. Commit your changes `(git commit -m 'Add some feature')`
4. In case of multiple commits squash them. You can find guide here: [how to squash commits](https://medium.com/@slamflipstrom/a-beginners-guide-to-squashing-commits-with-git-rebase-8185cf6e62ec)
4. Clear the checks and make sure build is successfull
5. Push your branch `(git push origin my-new-feature)`
6. Create a new Pull Request, following the template

## Coding style and CI
Currently we have basic github workflow setup for CI, which takes care of building the project and running the basic ktlint checks. For this project we are using [ktlint](https://ktlint.github.io/) code style, it provide the basic codestyle checks and formatter which can be  applied using basic gradle task. Make sure all your pull requests pass the CI build only then, it will be allowed to merge. Sometimes,when the build doesn't pass you can use these commands in your local terminal and check for the errors.

For Mac OS and Linux based, you can use the following commands:
* `./gradlew ktlintcheck` quality checks on your project’s code using defualt ktlint codestyle and generates reports from these checks.
* `./gradlew ktlintformat` automatic reformating of code based on ktlint codestyle
* `./gradlew lint` an predefined android lint checker.
* `./gradlew build`  provides a command line to execute build script.

For Windows, you can use the following commands:
* `gradlew ktlintcheck` quality checks on your project’s code using defualt ktlint codestyle and generates reports from these checks.
* `gradlew ktlintformat` automatic reformating of code based on ktlint codestyle
* `gradlew lint` an predefined android lint checker.
* `gradlew build`  provides a command line to execute build script.
