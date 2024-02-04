package com.sielotech.karaokeapp.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/** Provides Hilt with the dependencies it needs for preferences related injection. */
@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    /** Provides the [DataStore]<[Preferences]> used by the Preferences DataStore framework.
     * @return A singleton instance of [DataStore]<[Preferences]>.
     */
    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context) = context.dataStore
}