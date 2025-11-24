# Gemini Project Analysis: Ejemplo PM

## Project Overview

This is a simple Android application named "Ejemplo." The app serves as a demonstration of basic Android concepts, including:

*   **User Authentication:** A login screen that validates user credentials against a local SQLite database.
*   **Database Management:** The app uses a pre-packaged SQLite database (`ejemplo.db`) and includes a `DBHelper` class with methods for common CRUD (Create, Read, Update, Delete) operations. The `sqliteassethelper` library is used to manage the database asset.
*   **UI:** The app has two main activities: a login screen (`MainActivity`) and a main screen that displays a list of users (`Principal`). It uses a `ListView` and a custom `UsersAdapter` to display the user data.

The project is written in Java and built with Gradle.

## Building and Running

### Building the Project

To build the project, you can use the Gradle wrapper script included in the repository:

```bash
./gradlew build
```

### Running the Project

You can run the application on an Android emulator or a physical device using Android Studio or by executing the following Gradle command:

```bash
./gradlew installDebug
```

This will build and install the debug version of the application on a connected device.

### Testing the Project

The project includes both unit tests and instrumented tests.

*   **Unit tests:**

    ```bash
    ./gradlew test
    ```

*   **Instrumented tests:**

    ```bash
    ./gradlew connectedAndroidTest
    ```

## Development Conventions

*   **Database:** The application uses a pre-packaged SQLite database. The `DBHelper` class provides an abstraction layer for all database interactions.
*   **Activities:** The application follows a simple two-activity structure: `MainActivity` for login and `Principal` for displaying data.
*   **Adapters:** A custom `UsersAdapter` is used to populate the `ListView` in the `Principal` activity.
*   **Dependencies:** The project uses a small set of common Android libraries, including `appcompat`, `material`, `constraintlayout`, and `sqliteassethelper`.
*   **Security:** The `getAllUsersSafe()` method in `DBHelper` is used to avoid exposing user passwords to the UI. However, the application stores and validates passwords in plaintext, which is a security risk that should be addressed in a production application.
