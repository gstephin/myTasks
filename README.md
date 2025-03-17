📌 Task Management App
A task management app built using Jetpack Compose, Kotlin, and Room Database. It allows users to:
✅ Manage tasks with sorting and filtering options
🎨 Customize the app’s UI color
📌 Check the app version with proper exception handling
📊 View an animated progress bar for task completion
⚙️ Configure settings for color preferences and navigation

✨ Features
Task Management: Add, edit, delete, and organize tasks
Sorting & Filtering: Easily sort and filter tasks based on different criteria
Theme Customization: Choose a primary UI color and persist the selection
Progress Indicator: Animated circular progress bar to track task completion
App Version Display: Shows the app version with exception handling
Settings Screen: Manage UI theme and navigate back seamlessly
🛠 Setup Instructions
1️⃣ Clone the Repository
sh
Copy
Edit
git clone https://github.com/gstephin/myTasks.git
cd myTasks
2️⃣ Install Dependencies
Ensure you have the correct Android Studio and Gradle versions. Sync the project in Android Studio.

3️⃣ Build and Run
Open Android Studio
Import the project
Sync Gradle files
Run on an emulator or physical device
🎨 Design Rationale
1️⃣ UI & User Experience
Designed for simplicity and ease of use, the UI includes:

TopAppBar: Displays "Tasks" with settings and sorting/filtering options
Circular Progress Bar: Aesthetic task completion indicator
Settings Screen: Customization options for the app’s primary color (persisted using Room)
Version Info: Clean display of app version
Principles Followed:
✔️ Consistency – Intuitive design across UI components
✔️ Feedback – Visual updates for progress & settings changes
✔️ Customizability – Personalized themes for a better experience

2️⃣ State Management & Data Flow
Jetpack Compose: For UI construction
State & Flow: Manages UI updates reactively
Room Database: Stores task data and user preferences persistently
3️⃣ App Version Handling
Retrieves version info from the package manager
Graceful exception handling to prevent crashes
4️⃣ Color Customization
Displays color options in a LazyColumn
Selected color is highlighted with a checkmark
Stored in Room Database for persistence across sessions
📜 License
This project is licensed under the MIT License – see the LICENSE file for details.
