package com.app.mytasks.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mytasks.data.entities.BottomNavItem
import com.app.mytasks.data.entities.Task
import com.app.mytasks.viemodel.TaskViewModel

/**
 * TaskListScreen
 *
 *
 * Tasks screen with task list and options.
 *
 * @author Stephin
 * @date 2025-03-12
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onTaskClick: (Task) -> Unit,
    onAddTask: () -> Unit,
    onSettingsClick: () -> Unit // Add this
) {
    val navItems = listOf(
        BottomNavItem("Tasks", Icons.AutoMirrored.Filled.List),
        BottomNavItem("Overview", Icons.AutoMirrored.Filled.Send)
    )

    var selectedIndex by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    "Tasks",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }, actions = {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(36.dp) // Increase touch target
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(30.dp) // Increase icon size
                    )
                }
            })


        },
        floatingActionButton = { BouncyFAB(onAddTask) },
        snackbarHost = { SnackbarHost(snackbarHostState) },

        // 👇 Add Bottom Navigation here
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(item.title)
                        }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedIndex) {
                0 -> TaskTab(viewModel, onTaskClick, snackbarHostState)
                1 -> CryptoScreen()
            }
        }
    }

}


@Composable
fun BouncyFAB(onAddTask: () -> Unit) {
    var isClicked by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isClicked) 0.85f else 1f, // Shrinks slightly on click
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        finishedListener = { isClicked = false } // Reset after animation
    )

    FloatingActionButton(
        onClick = {
            isClicked = true
            onAddTask()
        }, modifier = Modifier.scale(scale) // Apply animation
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Task")
    }
}

data class DraggableItem(val index: Int)

// Updated FilterDropdown
