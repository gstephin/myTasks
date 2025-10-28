package com.app.mytasks.di

import android.app.Application
import androidx.room.Room
import com.app.mytasks.data.dao.TaskDao
import com.app.mytasks.data.dao.TaskDatabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): TaskDatabase =
        Room.databaseBuilder(
            app,
            TaskDatabase::class.java,
            "task_database"
        ).build()

    @Provides
    fun provideTaskDao(db: TaskDatabase): TaskDao = db.taskDao()
}