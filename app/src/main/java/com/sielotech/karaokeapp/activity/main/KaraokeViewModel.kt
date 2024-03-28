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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Manages the state of [KaraokeUI].
 * @param songsRepository A [SongsRepository] singleton provided by Hilt through DI.
 */
@HiltViewModel
internal class KaraokeViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val furiganaRepository: FuriganaRepository,
    private val preferencesRepository: PreferencesRepository,
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val mutableState = MutableStateFlow(MainActivityUiState())
    val uiState = mutableState.asStateFlow()

    init {
        checkLoginStatus()
        collectSongs()
    }

    private fun checkLoginStatus() = viewModelScope.launch {
        if (!authenticationRepository.isUserLoggedIn()) {
            _navigationEvent.emit(NavigationEvent.NavigateToLogin)
        }

        viewModelScope.launch {
            preferencesRepository.userEmail.collect { email ->
                val state = mutableState.value
                mutableState.value = MainActivityUiState(
                    loading = false,
                    email = email,
                    selectedIndex = state.selectedIndex,
                    currentSong = if (state.songs.isNotEmpty())
                        state.songs[state.selectedIndex]
                    else
                        null,
                    songs = state.songs,
                    isPlaying = false
                )
            }
        }
    }

    private fun collectSongs() {
        viewModelScope.launch {
            songsRepository.getAllSongsFlow().collect { songs ->
                if (songs.isNotEmpty()) {
                    if (mutableState.value.loading) {
                        mutableState.value = MainActivityUiState(
                            loading = false,
                            email = preferencesRepository.userEmail.last(),
                            selectedIndex = 0,
                            currentSong = songs[0],
                            songs = songs,
                            isPlaying = false
                        )
                    } else {
                        val state = mutableState.value
                        mutableState.value = MainActivityUiState(
                            loading = false,
                            email = preferencesRepository.userEmail.last(),
                            selectedIndex = state.selectedIndex,
                            currentSong = state.songs[state.selectedIndex],
                            songs = songs,
                            isPlaying = false
                        )
                    }
                }
            }
        }
    }


    suspend fun changeSong(index: Int) {
        val state = mutableState.value
        mutableState.value = MainActivityUiState(
            loading = false,
            email = preferencesRepository.userEmail.last(),
            selectedIndex = index,
            currentSong = state.songs[index],
            songs = state.songs,
            isPlaying = false
        )
    }

    internal class MainActivityUiState(
        val loading: Boolean = true,
        val email: String = "",
        val currentSong: Song? = null,
        val selectedIndex: Int = 0,
        val songs: List<Song> = listOf(),
        val isPlaying: Boolean = false,
    )

    sealed class NavigationEvent {
        data object NavigateToLogin : NavigationEvent()
    }
}