package com.app.mytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.mytasks.ui.screens.navigation.ParentNav
import com.app.mytasks.util.ColorPreferences
import com.app.mytasks.ui.theme.TaskManagerTheme
import com.app.mytasks.viemodel.AuthViewModel

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
    @Inject
    lateinit var colorPreferences: ColorPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // ✅ Hilt automatically provides this ViewModel
            val viewModel: TaskViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()

            TaskManagerTheme(colorPreferences = colorPreferences) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    ParentNav(viewModel, authViewModel)
                }
            }
        }
    }
}