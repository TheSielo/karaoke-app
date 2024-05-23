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

    /** Provides a different instance of [CoroutineScope] everytime it's called. */
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Job() + Dispatchers.IO)
    }
}