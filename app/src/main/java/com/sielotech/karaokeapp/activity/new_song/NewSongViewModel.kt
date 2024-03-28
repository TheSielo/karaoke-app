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
        MutableStateFlow<NewSongActivityUiState>(NewSongActivityUiState.Default)
    val newSongActivityUiState = mutableState.asStateFlow()

    fun addOrUpdateSong(title: String, jap: String, trans: String, url: String = "") {
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

    internal sealed class NewSongActivityUiState {
        data object Default: NewSongActivityUiState()
    }
}