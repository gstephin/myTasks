package com.app.mytasks.data

import android.content.Context
import androidx.room.*

/**
 * TaskDao.kt
 *
 * Database access object for the Task entity.
 *
 * @author Stephin
 * @date 2025-03-12
 */

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(task: List<Task>)

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)
}

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}