package com.app.mytasks.domain.repository

import com.app.mytasks.data.entities.Task
import com.app.mytasks.data.dao.TaskDao
import com.app.mytasks.data.remote.ApiService
import javax.inject.Inject

/**
 * TaskRepositoryImpl
 *
 * @author stephingeorge
 * @date 28/10/2025
 */
class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao,
    private val api: ApiService
) : TaskRepository {

    override suspend fun getAllTasks(): List<Task> = dao.getAllTasks()

    override suspend fun insertTask(task: Task) = dao.insert(task)

    override suspend fun insertAllTask(task: List<Task>) = dao.insertAll(task)

    override suspend fun updateTask(task: Task) = dao.update(task)

    override suspend fun getTaskCount(): Int = dao.getTaskCount()


    override suspend fun deleteTask(task: Task) = dao.delete(task)
}
