package com.app.mytasks


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.mytasks.util.ColorPreferences
import com.app.mytasks.ui.screens.SettingsScreen
import com.app.mytasks.ui.screens.TaskCreationScreen
import com.app.mytasks.ui.screens.TaskDetailScreen
import com.app.mytasks.ui.screens.TaskListScreen
import com.app.mytasks.ui.theme.TaskManagerTheme
import com.app.mytasks.viemodel.TaskViewModel

/**
 * TaskManagerApp
 *
 *
 * TaskManager Application entry point.
 *
 * @author Stephin
 * @date 2025-03-12
 */
@Composable
fun TaskManagerApp(viewModel: TaskViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val colorPreferences = remember { ColorPreferences(context) }
    val density = LocalDensity.current
    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val defaultOffset = Offset(screenWidthPx / 2, screenHeightPx / 2)
    TaskManagerTheme(colorPreferences = colorPreferences) {
        NavHost(navController, startDestination = "task_list") {
            composable("task_list") {
                TaskListScreen(
                    viewModel = viewModel,
                    onTaskClick = { navController.navigate("task_detail/${it.id}") },
                    onAddTask = { navController.navigate("task_creation") },
                    onSettingsClick = { navController.navigate("settings") } // Add this
                )
            }
            composable("task_creation") {
                TaskCreationScreen(
                    onSave = { viewModel.addTask(it); navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable("task_detail/{taskId}") { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
                val task = viewModel.tasks.value.find { it.id == taskId } ?: return@composable
                TaskDetailScreen(
                    task = task,
                    onSave = { viewModel.updateTask(it); navController.popBackStack() },
                    onDelete = { viewModel.deleteTask(task); navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                    clickOffset = defaultOffset
                )
            }
            composable("settings") {
                SettingsScreen(
                    colorPreferences = colorPreferences,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}