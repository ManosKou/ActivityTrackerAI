# ActivityTrackerAI

An Android application for tracking daily physical activity, with
step-count statistics, personal activity goals and a chat interface.

The original academic version integrated a Google Dialogflow chatbot.
In this public version, live chatbot responses have been replaced
with an informational message.

Developed by Emmanouil Koutsokostas as a final-year project for the
BSc (Hons) Computer Science programme at the University of East London,
delivered through Metropolitan College, in 2025.

## Features

- User registration and login with email and password.
- Step counting using the device's built-in step counter sensor.
- Personal daily step goals.
- Activity statistics with daily, weekly, monthly and yearly views.
- Chat interface with an informational response in the public version.
- Interactive map displaying the user's current location.
- Cloud storage of step-count data using Firebase Realtime Database.
- Local storage of chat messages using Room.

## Technology Stack

- Language: Kotlin 1.9.24
- User interface: Jetpack Compose, Material 3 and AndroidX
- Authentication: Firebase Authentication
- Cloud database: Firebase Realtime Database
- Local database: Room
- Maps and location: Google Maps SDK for Android and Google Play Services Location
- Networking and JSON: OkHttp and Gson
- Original chatbot integration: Google Dialogflow via REST API
- Development tools: Android Studio and Gradle
- Android Gradle Plugin: 8.10.0

## Requirements

- Android Studio compatible with Android Gradle Plugin 8.10.0.
- JDK 17.
- Android SDK Platform 34.
- An Android device or emulator running Android 8.0 (API 26) or later.
- Your own Firebase and Google Maps configuration.

A physical device with a step counter sensor is recommended for testing
step tracking. Internet access is required for Firebase and Google Maps.

Dialogflow credentials are not required for this public version.

## Project Structure

- `app/src/main/java/com/example/activitytrackerai/` — application source code.
- `app/src/main/res/` — application resources.
- `app/src/main/AndroidManifest.xml` — permissions and application configuration.
- `app/build.gradle.kts` — Android build configuration and dependencies.
- `gradle/libs.versions.toml` — dependency version catalogue.

## Local Setup

Personal cloud configuration files and service-account credentials
are excluded from this repository.

To run the application, configure your own Firebase project and
Google Maps API key as described below.

### 1. Open the project

Clone or download the repository and open its root folder in Android Studio.

### 2. Configure Firebase

- Create your own Firebase project.
- Register an Android application with the package name:
  `com.example.activitytrackerai`.
- Enable Email/Password authentication.
- Create a Firebase Realtime Database.
- Configure database access rules so that authenticated users can
  access only their own data.
- Download your own `google-services.json` and place it in `app/`.
- Replace the database URLs in `FirebaseUtils.kt` with your own
  Realtime Database URL.

The `app/google-services.json` file is excluded through `.gitignore`.
Keep your local configuration out of commits.

### 3. Configure Google Maps

Enable the Maps SDK for Android in your Google Cloud project.

Set your own appropriately restricted API key in the
`com.google.android.geo.API_KEY` metadata entry in:

`app/src/main/AndroidManifest.xml`

Use a key restricted to your Android application and the required Maps API.
Do not commit your personal Maps API key.

### 4. Chatbot in the public version

The original academic application used Google Dialogflow for
Greek-language chatbot interaction.

In this public version, `DialogflowHelper.kt` returns a fixed informational
message instead of calling Dialogflow. No service-account credentials
are included or required.

The helper no longer references the excluded
`dialogflow_credentials.json` resource.

To restore live chatbot functionality, implement a secure backend
that communicates with Dialogflow. Service-account private keys
must remain on the backend and must not be bundled in the Android application.

The original Dialogflow agent configuration is not included.

### 5. Build and run

After completing the Firebase and Google Maps configuration:

- Synchronise the project with Gradle.
- Select an Android device or emulator.
- Run the application.
- Grant activity recognition and location permissions when requested.
- Register an account using email and password.
- Explore the Chat, Statistics, Goals, Map and Profile screens.

Step tracking requires a device with a compatible step counter sensor.
The chat screen displays an informational message rather than a live
Dialogflow response.

## Project Status

The original configured application was built and presented to academic
examiners as part of the final-year project in 2025.

This public version preserves the Android application source code,
with live Dialogflow calls replaced by an informational response.
Personal cloud configuration files are excluded.

Firebase and Google Maps require your own configuration.
The modified public version has not yet been verified with a new build.

The implementation included here targets Android.

## Future Improvements

- Restore live chatbot functionality through a secure backend.
- Move cloud configuration values out of source files.
- Expand automated testing.
- Improve error handling and offline behaviour.