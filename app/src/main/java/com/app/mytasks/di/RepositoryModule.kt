package com.example.mytasks.di

import com.app.mytasks.data.dao.TaskDao
import com.app.mytasks.data.remote.ApiService
import com.app.mytasks.domain.repository.TaskRepository
import com.app.mytasks.domain.repository.TaskRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTaskRepository(
        dao: TaskDao,
        api: ApiService
    ): TaskRepository = TaskRepositoryImpl(dao, api)
}
