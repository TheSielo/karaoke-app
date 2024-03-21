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

    private val mutableState = MutableStateFlow<MainActivityUiState>(MainActivityUiState.Loading)
    val uiState= mutableState.asStateFlow()

    init {
        checkLoginStatus()
        collectSongs()
    }

    private fun checkLoginStatus() = viewModelScope.launch {
        if (!authenticationRepository.isUserLoggedIn()) {
            _navigationEvent.emit(NavigationEvent.NavigateToLogin)
        }

        viewModelScope.launch {
            preferencesRepository.setIsFirstAccess()
        }
    }

    private fun collectSongs() {
        viewModelScope.launch {
            songsRepository.getAllSongsFlow().collect { songs ->
                when (mutableState.value) {
                    is MainActivityUiState.Loading -> {
                        if (songs.isNotEmpty()) {
                            mutableState.value = MainActivityUiState.Default(
                                selectedIndex = 0,
                                currentSong = songs[0],
                                songs = songs,
                                isPlaying = false
                            )
                        }
                    }

                    is MainActivityUiState.Default -> {
                        val state = (mutableState.value as MainActivityUiState.Default)
                        mutableState.value = MainActivityUiState.Default(
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


    fun changeSong(index: Int) {
        val state = (mutableState.value as MainActivityUiState.Default)
        mutableState.value = MainActivityUiState.Default(
            selectedIndex = index,
            currentSong = state.songs[index],
            songs = state.songs,
            isPlaying = false
        )
    }

    fun playButtonPressed() {
        val state = (mutableState.value as MainActivityUiState.Default)
        mutableState.value = MainActivityUiState.Default(
            selectedIndex = state.selectedIndex,
            currentSong = state.currentSong,
            songs = state.songs,
            isPlaying = !state.isPlaying
        )
    }

    internal sealed class MainActivityUiState {
        data class Default(
            val currentSong: Song,
            val selectedIndex: Int,
            val songs: List<Song>,
            val isPlaying: Boolean,
        ) : MainActivityUiState()

        data object Loading : MainActivityUiState()
    }

    sealed class NavigationEvent {
        data object NavigateToLogin : NavigationEvent()
    }
}