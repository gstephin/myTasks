package com.app.mytasks.ui.screens.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.geometry.Offset
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.app.mytasks.ui.screens.TaskDetailScreen
import com.app.mytasks.ui.screens.MessageScreen
import com.app.mytasks.ui.screens.SettingsScreen
import com.app.mytasks.ui.screens.TaskCreationScreen
import com.app.mytasks.ui.screens.TasksScreen
import com.app.mytasks.ui.screens.home.HomeScreen
import com.app.mytasks.util.ColorPreferences
import com.app.mytasks.viemodel.AuthViewModel
import com.app.mytasks.viemodel.TaskViewModel


@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.bottomNavGraph(
    navController: NavHostController,
    viewModel: TaskViewModel,
    authViewModel: AuthViewModel,
    colorPreferences: ColorPreferences,
    defaultOffset: Offset
) {

    navigation<Destinations.BottomNavGraph>(
        startDestination = Destinations.Home,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {

        composable<Destinations.Details> { backStackEntry ->
            // ✅ This gives you the actual typed object
            val destination = backStackEntry.toRoute<Destinations.Details>()
            val taskId = destination.taskId
            val task = viewModel.tasks.value.find { it.id == taskId } ?: return@composable

            TaskDetailScreen(
                task = task,
                onSave = { viewModel.updateTask(it); navController.popBackStack() },
                onDelete = { viewModel.deleteTask(task); navController.popBackStack() },
                onBack = { navController.popBackStack() },
                clickOffset = defaultOffset
            )
        }
        composable<Destinations.Home> {
            HomeScreen(navController)
        }
        composable<Destinations.Tasks> {
            TasksScreen(viewModel, onTaskClick = { navController.navigate(Destinations.AddTask) })
        }
        composable<Destinations.AddTask> {
            TaskCreationScreen(
                onSave = { viewModel.addTask(it); navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable<Destinations.Message> {
            MessageScreen()
        }
        composable<Destinations.Profile> {
            SettingsScreen(
                colorPreferences = colorPreferences,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Destinations.Login) {
                        popUpTo(Destinations.BottomNavGraph) { inclusive = true }
                    }
                }
            )
        }
    }
}