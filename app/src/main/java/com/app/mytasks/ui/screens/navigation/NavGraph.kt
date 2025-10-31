package com.app.mytasks.ui.screens.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.app.mytasks.ui.screens.TaskDetailScreen
import com.app.mytasks.ui.screens.MessageScreen
import com.app.mytasks.ui.screens.ProfileScreen
import com.app.mytasks.ui.screens.SettingsScreen
import com.app.mytasks.ui.screens.TaskCreationScreen
import com.app.mytasks.ui.screens.TasksScreen
import com.app.mytasks.ui.screens.home.HomeScreen
import com.app.mytasks.util.ColorPreferences
import com.app.mytasks.viemodel.AuthViewModel
import com.app.mytasks.viemodel.TaskViewModel


fun NavGraphBuilder.bottomNavGraph(
    navController: NavHostController,
    viewModel: TaskViewModel,
    authViewModel: AuthViewModel,
    colorPreferences: ColorPreferences
) {
    navigation<Destinations.BottomNavGraph>(
        startDestination = Destinations.Home,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable<Destinations.Home> {
            HomeScreen(colorPreferences, authViewModel)
        }
        composable<Destinations.Tasks> {
            TasksScreen(viewModel, onTaskClick = {navController.navigate(Destinations.AddTask)},)
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