package com.sielotech.karaokeapp.activity.karaoke

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sielotech.karaokeapp.api.FuriganaRepository
import com.sielotech.karaokeapp.database.SongsRepository
import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
                mutableState.value = mutableState.value.copyWith(
                    email = email
                )
            }
        }
    }

    private fun collectSongs() {
        viewModelScope.launch {
            songsRepository.getAllSongsFlow().collect { songs ->
                val state = mutableState.value
                mutableState.value = mutableState.value.copyWith(
                    songs = songs,
                    selectedJapLines = getJapLines(state.selectedIndex),
                    selectedTransLines = getTransLines(state.selectedIndex),
                )
            }
        }
    }

    suspend fun deleteSong() {
        songsRepository.deleteSong(uiState.value.songs[uiState.value.selectedIndex])
        changeSong(0)
    }

    fun changeSong(index: Int) {
        mutableState.value = uiState.value.copyWith(
            selectedIndex = index,
            furigana = listOf(),
            selectedJapLines = getJapLines(index),
            selectedTransLines = getTransLines(index),
            loadingFurigana = false

        )
    }

    suspend fun getFurigana() {
        mutableState.value = uiState.value.copyWith(
            loadingFurigana = true
        )
        val text = uiState.value.songs[uiState.value.selectedIndex].japaneseText
        val regex = "\n+".toRegex()
        val cleanedText = text.replace(regex, "\n")
        val furigana = furiganaRepository.getFurigana(cleanedText)
        val furiganaList = arrayListOf<ArrayList<List<String>>>()
        furiganaList.add(arrayListOf())
        for (list in furigana) {
            if (list[0] == "\n") {
                furiganaList.add(arrayListOf())
                continue
            } else {
                furiganaList.last().add(list)
            }
        }
        mutableState.value = uiState.value.copyWith(
            furigana = furiganaList,
            loadingFurigana = false
        )
    }

    private fun getJapLines(index: Int): List<String> {
        return if (uiState.value.songs.isNotEmpty()) {
            val text = uiState.value.songs[index].japaneseText
            val regex = "\n+".toRegex()
            val cleanedText = text.replace(regex, "\n")
            cleanedText.split("\n")
        } else {
            listOf()
        }
    }

    private fun getTransLines(index: Int): List<String> {
        return if (uiState.value.songs.isNotEmpty()) {
            val text = uiState.value.songs[index].translatedText
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
        val furigana: List<List<List<String>>> = listOf(),
        val loadingFurigana: Boolean = false,
    ) {
        fun copyWith(
            email: String? = null,
            selectedIndex: Int? = null,
            selectedJapLines: List<String>? = null,
            selectedTransLines: List<String>? = null,
            songs: List<Song>? = null,
            furigana: List<List<List<String>>>? = null,
            loadingFurigana: Boolean? = null,
        ): KaraokeUiState {
            return KaraokeUiState(
                email = email ?: this.email,
                selectedIndex = selectedIndex ?: this.selectedIndex,
                selectedJapLines = selectedJapLines ?: this.selectedJapLines,
                selectedTransLines = selectedTransLines ?: this.selectedTransLines,
                songs = songs ?: this.songs,
                furigana = furigana ?: this.furigana,
                loadingFurigana = loadingFurigana ?: this.loadingFurigana,
            )
        }
    }
}