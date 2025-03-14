Task Management App
A task management app built using Jetpack Compose, Kotlin, and Room Database. It allows users to
manage their tasks, select colors for the app's UI, and check the app's version.

Features
Task management with sorting and filtering options.
Color preference settings to customize the app's theme.
Display of the app version with proper exception handling.
Animated progress bar indicating task completion.
Settings screen for managing primary color preferences and back navigation.

🛠 Setup Instructions

1. Clone the Repository
   bash
   Copy code
   git clone https://github.com/gstephin/myTasks.git
   cd myTasks
2.

Install Dependencies
Make sure to use the correct version of Android Studio and Gradle. You can install dependencies by
syncing your project in Android Studio.

Gradle
Make sure to add the necessary dependencies in your build.gradle files.

3. Build and Run
   After the project is set up, you can build and run the app using Android Studio.

Open Android Studio.
Import the project.
Sync Gradle files.
Run on an emulator or a physical device.

🖌 Design Rationale

1. UI and User Experience
   The app has been designed with simplicity and ease of use in mind. Key UI elements include:

TopAppBar: Displays the title "Tasks" and provides access to settings and sorting/filtering.
Circular Progress Bar: A visually appealing progress indicator shows the percentage of tasks
completed.
Settings Screen: Allows users to customize the primary color of the app. The color preference is
stored using Room Database for persistence.
Version Information: Displays the app version in a user-friendly format.
The primary design principles are:

Consistency: All UI components follow a consistent design pattern, making the app intuitive.
Feedback: Users receive clear feedback, such as progress updates and color selection confirmations.
Customizability: The ability to customize the primary color enhances the user's experience by
offering personalization.

2. State Management and Data Flow
   We are using Jetpack Compose for UI construction and Room Database for local persistence of task
   data and user preferences.

State Management: State is managed using State and Flow to handle UI updates reactively.
Room Database: Task data is stored in a local database for persistence across app sessions. The
user's primary color preference is also saved in Room and used throughout the app.

3. App Version Handling
   The app version is fetched from the package manager and displayed in the Settings Screen.
   If the version info cannot be fetched (e.g., due to missing package info), the app handles the
   exception gracefully without crashing.
4. Color Customization
   The app allows users to choose a primary color for the UI. The list of color options is displayed
   in a LazyColumn, and the selected color is highlighted with a checkmark.
   The selected color is stored in Room Database and used throughout the app, ensuring persistence
   even after app restarts.
   📝 License
   This project is licensed under the MIT License - see the LICENSE file for details.




