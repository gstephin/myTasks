package com.app.mytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.mytasks.util.ColorPreferences
import com.app.mytasks.data.TaskDatabase
import com.app.mytasks.ui.theme.TaskManagerTheme

import com.app.mytasks.viemodel.TaskViewModel
/**
 * MainActivity
 *
 * Main activity for the task manager application.
 *
 * @author Stephin
 * @date 2025-03-12
 */


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = TaskDatabase.getDatabase(this)
        val viewModel = TaskViewModel(db.taskDao(), application = application)
        val colorPreferences = ColorPreferences(this) // Initialize here

        setContent {
            TaskManagerTheme(colorPreferences = colorPreferences) {
                TaskManagerApp(viewModel)
            }
        }
    }
}