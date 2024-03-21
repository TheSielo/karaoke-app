package com.sielotech.karaokeapp.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    /** Indicates if this is the first time the user open the app. */
    private val isFirstAccessKey = booleanPreferencesKey("is_first_access")

    /** A Flow that emits the current value assigned to [isFirstAccessKey]. */
    val isFirstAccess: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[isFirstAccessKey] ?: true
        }

    /** Set [isFirstAccessKey] to false. */
    suspend fun setIsFirstAccess() {
        dataStore.edit { settings ->
            settings[isFirstAccessKey] = false
        }
    }
}