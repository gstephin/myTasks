package com.app.mytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.mytasks.util.ColorPreferences
import com.app.mytasks.ui.theme.TaskManagerTheme

import com.app.mytasks.viemodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * MainActivity
 *
 * Main activity for the task manager application.
 *
 * @author Stephin
 * @date 2025-03-12
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
        @Inject lateinit var colorPreferences: ColorPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // ✅ Hilt automatically provides this ViewModel
            val viewModel: TaskViewModel = hiltViewModel()

            TaskManagerTheme(colorPreferences = colorPreferences) {
                TaskManagerApp(viewModel)
            }
        }
    }
}