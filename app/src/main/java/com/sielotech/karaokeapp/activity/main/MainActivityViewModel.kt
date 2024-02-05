package com.sielotech.karaokeapp.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sielotech.karaokeapp.api.FuriganaRepository
import com.sielotech.karaokeapp.auth.AuthenticationRepository
import com.sielotech.karaokeapp.database.SongsRepository
import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/** Manages the state of [MainActivity].
 * @param songsRepository A [SongsRepository] singleton provided by Hilt through DI.
 */
@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val furiganaRepository: FuriganaRepository,
    private val preferencesRepository: PreferencesRepository,
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val mutableState = MutableStateFlow<MainActivityUiState>(MainActivityUiState.Loading)
    val mainActivityUiState = mutableState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() = viewModelScope.launch {
        if (!authenticationRepository.isUserLoggedIn()) {
            _navigationEvent.emit(NavigationEvent.NavigateToLogin)
        }
    }

    /*fun getSongs(): List<Song> {
        return songsRepository.getSongs()
    }*/

    fun addOrUpdateSong(title: String, jap: String, trans: String, url: String) {
        val uuid = UUID.randomUUID().toString()
        val song = Song(
            uuid = uuid, title = title, japaneseText = jap, translatedText = trans, url = url
        )
        viewModelScope.launch {
            val userId = authenticationRepository.userId
            if (userId != null) {
                songsRepository.addOrUpdateSong(song)
            }
        }
    }

    internal sealed class MainActivityUiState {
        data class Default(
            val loggedIn: Boolean
        ) : MainActivityUiState()

        data object Loading : MainActivityUiState()
    }

    sealed class NavigationEvent {
        data object NavigateToLogin : NavigationEvent()
    }
}