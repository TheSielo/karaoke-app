package com.sielotech.karaokeapp.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sielotech.karaokeapp.api.FuriganaRepository
import com.sielotech.karaokeapp.auth.AuthenticationRepository
import com.sielotech.karaokeapp.database.SongsRepository
import com.sielotech.karaokeapp.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.log

/** Manages the state of [MainActivity].
 * @param songsRepository A [SongsRepository] singleton provided by Hilt through DI.
 */
@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val furiganaRepository: FuriganaRepository,
    private val preferencesRepository: PreferencesRepository,
    private val authenticationRepository: AuthenticationRepository,
): ViewModel() {
    private val mutableState = MutableStateFlow<MainActivityUiState>(MainActivityUiState.Loading)
    val mainActivityUiState = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.setIsFirstAccess()
            val loggedIn = authenticationRepository.isUserLoggedIn()
            Timber.d("logged in: $loggedIn")
            mutableState.value = MainActivityUiState.Default(loggedIn = loggedIn)
        }
    }

    /*fun getSongs(): List<Song> {
        return songsRepository.getSongs()
    }*/


    internal sealed class MainActivityUiState {
        data class Default(
            val loggedIn: Boolean
        ) : MainActivityUiState()
        data object Loading: MainActivityUiState()
    }
}