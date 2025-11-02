package com.app.mytasks.domain.repository

import com.app.mytasks.data.entities.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

// domain/repository/TaskRepository.kt
interface TaskRepository {
    suspend fun getAllTasks(): List<Task>
    suspend fun insertTask(task: Task)
    suspend fun insertAllTask(task: List<Task>)
    suspend fun updateTask(task: Task)
    suspend fun getTaskCount(): Int
    suspend fun deleteTask(task: Task)
    suspend fun getAllTasksFlow(): Flow<List<Task>>

    suspend fun getTasksByDate(startOfDay: Long, endOfDay: Long): Flow<List<Task>>

}
