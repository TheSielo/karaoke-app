package com.sielotech.karaokeapp.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    /** Indicates if a user has successfully authenticated. */
    private val isLoggedInKey = booleanPreferencesKey("is_logged_in")

    /** Stores the currently logged in user's email address. */
    private val userEmailKey = stringPreferencesKey("user_email")


    /** A Flow that emits the current value assigned to [isLoggedIn]. */
    val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[isLoggedInKey] ?: false
        }

    /** A Flow that emits the current value assigned to [userEmail]. */
    val userEmail: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[userEmailKey] ?: ""
        }

   /** Set [isLoggedInKey] value. */
    suspend fun setIsLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { settings ->
            settings[isLoggedInKey] = isLoggedIn
        }
    }

    /** Set [userEmail] value. */
    suspend fun setUserEmail(email: String) {
        dataStore.edit { settings ->
            settings[userEmailKey] = email
        }
    }
}