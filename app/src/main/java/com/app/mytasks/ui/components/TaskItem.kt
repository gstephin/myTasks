package com.app.mytasks.ui.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.app.mytasks.data.entities.Priority
import com.app.mytasks.data.entities.Task
import java.text.SimpleDateFormat
import java.util.*

@Composable

fun TaskItem(
    task: Task,
    onClick: (Offset) -> Unit,  // Modified to include click position
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                // Fallback clickable on the Card level
                onClick = { onClick(Offset.Zero) } // Default to center if not overridden
            )
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple( // Replace rememberRipple with ripple()
                        bounded = true,
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    // This block won't execute due to pointerInput override,
                    // but it's here as a fallback
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        onClick(offset) // Capture the actual click position
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = null,
                modifier = Modifier.semantics { contentDescription = "Task completion status" }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                task.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row {
                    Text(
                        text = task.priority.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = when (task.priority) {
                            Priority.HIGH -> MaterialTheme.colorScheme.error
                            Priority.MEDIUM -> MaterialTheme.colorScheme.secondary
                            Priority.LOW -> MaterialTheme.colorScheme.primary
                        }
                    )
                    task.dueDate?.let { dueDate ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Due: ${
                                SimpleDateFormat(
                                    "MM/dd/yyyy",
                                    Locale.getDefault()
                                ).format(Date(dueDate))
                            }",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { onDelete(task) },
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}