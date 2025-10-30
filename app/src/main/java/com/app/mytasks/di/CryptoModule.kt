package com.app.mytasks.di

import com.app.mytasks.data.remote.PoloniexWebSocketClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * CryptoModule
 *
 * @author stephingeorge
 * @date 30/10/2025
 */
@Module
@InstallIn(SingletonComponent::class)
object CryptoModule {

    @Provides
    @Singleton
    fun providePoloniexClient(): PoloniexWebSocketClient = PoloniexWebSocketClient()



}