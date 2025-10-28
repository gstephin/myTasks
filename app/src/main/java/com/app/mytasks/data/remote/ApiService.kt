package com.app.mytasks.data.remote

import com.app.mytasks.data.entities.Task
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // 🔹 Get all tasks
    @GET("tasks")
    suspend fun getAllTasks(): Response<List<Task>>

    // 🔹 Get a single task by ID
    @GET("tasks/{id}")
    suspend fun getTaskById(
        @Path("id") id: Int
    ): Response<Task>

    // 🔹 Create a new task
    @POST("tasks")
    suspend fun createTask(
        @Body task: Task
    ): Response<Task>

    // 🔹 Update existing task
    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: Int,
        @Body task: Task
    ): Response<Task>

    // 🔹 Delete a task
    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") id: Int
    ): Response<Unit>
}
