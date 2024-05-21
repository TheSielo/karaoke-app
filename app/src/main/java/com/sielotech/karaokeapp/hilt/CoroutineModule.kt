package com.sielotech.karaokeapp.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/** Provides Hilt with the dependencies it needs for coroutine related injection. */
@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Job() + Dispatchers.IO)
    }
}