package com.sielotech.karaokeapp.activity.new_song

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sielotech.karaokeapp.auth.AuthenticationRepository
import com.sielotech.karaokeapp.database.SongsRepository
import com.sielotech.karaokeapp.database.dao.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class NewSongViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {

    private val mutableState =
        MutableStateFlow<NewSongActivityUiState>(NewSongActivityUiState.Default(step = CurrentStep.Japanese))
    val newSongActivityUiState = mutableState.asStateFlow()

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

    fun nextPressed(japText: String, transText: String, title: String, url: String) {
        val state = newSongActivityUiState.value as NewSongActivityUiState.Default
        val newStep = when(state.step) {
            CurrentStep.Japanese -> CurrentStep.Translation
            CurrentStep.Translation -> CurrentStep.Url
            CurrentStep.Url -> {
                addOrUpdateSong(
                    title, japText, transText, url,
                )
                CurrentStep.Completed
            }
            CurrentStep.Completed -> CurrentStep.Completed
        }
        mutableState.value = NewSongActivityUiState.Default(step = newStep)
    }

    fun backPressed() {
        val state = newSongActivityUiState.value as NewSongActivityUiState.Default
        val newStep = when(state.step) {
            CurrentStep.Japanese -> CurrentStep.Japanese
            CurrentStep.Translation -> CurrentStep.Japanese
            CurrentStep.Url -> CurrentStep.Translation
            CurrentStep.Completed -> CurrentStep.Completed
        }
        mutableState.value = NewSongActivityUiState.Default(step = newStep)
    }

    enum class CurrentStep {
        Japanese, Translation, Url, Completed,
    }

    internal sealed class NewSongActivityUiState {
        data class Default(
            val step: CurrentStep,
        ) : NewSongActivityUiState()
    }
}