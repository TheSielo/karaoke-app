package com.sielotech.karaokeapp.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sielotech.karaokeapp.api.FuriganaRepository
import com.sielotech.karaokeapp.database.SongsRepository
import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
) : ViewModel() {

    private val mutableState = MutableStateFlow(KaraokeUiState())
    val uiState = mutableState.asStateFlow()

    init {
        checkLoginData()
        collectSongs()
    }

    private fun checkLoginData() = viewModelScope.launch {
        viewModelScope.launch {
            preferencesRepository.userEmail.collect { email ->
                val state = mutableState.value
                mutableState.value = KaraokeUiState(
                    email = email,
                    selectedIndex = state.selectedIndex,
                    songs = state.songs,
                    furigana = state.furigana,
                    selectedJapLines = state.selectedJapLines,
                    selectedTransLines = state.selectedTransLines,
                )
            }
        }
    }

    private fun collectSongs() {
        viewModelScope.launch {
            songsRepository.getAllSongsFlow().collect { songs ->
                val state = mutableState.value
                mutableState.value = KaraokeUiState(
                    email = state.email,
                    selectedIndex = state.selectedIndex,
                    songs = songs,
                    furigana = state.furigana,
                    selectedJapLines = getJapLines(state.selectedIndex),
                    selectedTransLines = getTransLines(state.selectedIndex),
                )
            }
        }
    }


    fun changeSong(index: Int) {
        val state = mutableState.value
        mutableState.value = KaraokeUiState(
            email = state.email,
            selectedIndex = index,
            songs = state.songs,
            furigana = listOf(),
            selectedJapLines = getJapLines(index),
            selectedTransLines = getTransLines(index),
        )
    }

    suspend fun getFurigana() {
        val text = mutableState.value.songs[mutableState.value.selectedIndex].japaneseText
        val regex = "\n+".toRegex()
        val cleanedText = text.replace(regex, "\n")
        val furigana = furiganaRepository.getFurigana(cleanedText)
        val furiganaList = arrayListOf<ArrayList<List<String>>>()
        furiganaList.add(arrayListOf())
        for(list in furigana) {
            if (list[0] == "\n") {
                furiganaList.add(arrayListOf())
                continue
            } else {
                furiganaList.last().add(list)
            }
        }
        val state = mutableState.value
        mutableState.value = KaraokeUiState(
            email = state.email,
            selectedIndex = state.selectedIndex,
            songs = state.songs,
            furigana = furiganaList,
            selectedJapLines = state.selectedJapLines,
            selectedTransLines = state.selectedTransLines,
        )
    }

    private fun getJapLines(index: Int): List<String> {
        return if(mutableState.value.songs.isNotEmpty()) {
            val text = mutableState.value.songs[index].japaneseText
            val regex = "\n+".toRegex()
            val cleanedText = text.replace(regex, "\n")
            cleanedText.split("\n")
        } else {
            listOf()
        }
    }

    private fun getTransLines(index: Int): List<String> {
        return if(mutableState.value.songs.isNotEmpty()) {
            val text = mutableState.value.songs[index].translatedText
            val regex = "\n+".toRegex()
            val cleanedText = text.replace(regex, "\n")
            cleanedText.split("\n")
        } else {
            listOf()
        }
    }

    internal class KaraokeUiState(
        val email: String = "",
        val selectedIndex: Int = 0,
        val selectedJapLines: List<String> = listOf(),
        val selectedTransLines: List<String> = listOf(),
        val songs: List<Song> = listOf(),
        val furigana: List<List<List<String>>> = listOf()
    )
}