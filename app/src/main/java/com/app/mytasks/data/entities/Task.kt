package com.app.mytasks.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Task.kt
 *
 * Represents a task entity for the application.
 *
 * @author Stephin
 * @date 2025-03-12
 */

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    val priority: Priority = Priority.LOW,
    val dueDate: Long? = null, // Milliseconds since epoch
    val isCompleted: Boolean = false
)

enum class Priority {
    LOW, MEDIUM, HIGH
}
