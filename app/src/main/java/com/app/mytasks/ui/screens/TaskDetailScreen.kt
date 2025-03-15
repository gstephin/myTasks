package com.app.mytasks.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.app.mytasks.data.Task
import com.app.mytasks.util.CircularRevealShape
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * TaskDetailScreen
 *
 *
 * Task detail screen with task details and options.
 *
 * @author Stephin
 * @date 2025-03-12
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    task: Task,
    clickOffset: Offset,
    onSave: (Task) -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    val revealAnimation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        revealAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }
    var isCompleted by remember { mutableStateOf(task.isCompleted) }

    // Wrap Scaffold in Box with clip for circular reveal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .clip(
                if (revealAnimation.value > 0f)
                    CircularRevealShape(revealAnimation.value, clickOffset)
                else
                    RectangleShape // Avoids clipping to zero size initially
            )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Task Details") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                task.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "Priority: ${task.priority}",
                    style = MaterialTheme.typography.bodyMedium
                )
                task.dueDate?.let {
                    Text(
                        text = "Due: ${
                            SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(it)
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it },
                        modifier = Modifier.semantics {
                            contentDescription = "Mark as completed"
                        }
                    )
                    Text("Completed")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSave(task.copy(isCompleted = isCompleted)) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete")
                    }
                    Button(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }
                }
            }
        }
    }
}

// Same CircularRevealShape as before
