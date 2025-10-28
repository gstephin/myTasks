package com.app.mytasks.di

import android.app.Application
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
        TaskDatabase.getDatabase(app)

    @Provides
    fun provideTaskDao(db: TaskDatabase): TaskDao = db.taskDao()

}
