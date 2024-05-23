package com.sielotech.karaokeapp.activity.karaoke

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sielotech.karaokeapp.api.FuriganaRepository
import com.sielotech.karaokeapp.database.SongsRepository
import com.sielotech.karaokeapp.database.entity.Song
import com.sielotech.karaokeapp.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Manages the state of [KaraokeUI].
 * @param songsRepository A [SongsRepository] singleton provided by Hilt through DI.
 * @param furiganaRepository A [FuriganaRepository] singleton provided by Hilt through DI.
 * @param preferencesRepository A [PreferencesRepository] singleton provided by Hilt through DI.
 */
@HiltViewModel
internal class KaraokeViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val furiganaRepository: FuriganaRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    /** The mutable state representing the state of KaraokeUI */
    private val mutableState = MutableStateFlow(KaraokeUiState())
    /** The publicly accessible immutable state representing the state of KaraokeUI */
    val uiState = mutableState.asStateFlow()

    fun initialize() {
        songsRepository.initialize()
        checkLoginData()
        collectSongs()
    }

    /** Retrieves the currently logged in user's data and updates the state with it. */
    private fun checkLoginData() = viewModelScope.launch {
        viewModelScope.launch {
            preferencesRepository.userEmail.collect { email ->
                mutableState.value = mutableState.value.copyWith(
                    email = email
                )
            }
        }
    }

    /** Retrieves the user's songs and updates the state with them. It also updates The japanese
     * and translated lines using the currently selected song index. */
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

    /** Deletes the currently selected song. */
    suspend fun deleteSong() {
        songsRepository.deleteSong(uiState.value.songs[uiState.value.selectedIndex])
        changeSong(0)
    }

    /** Selects and show the details of different song.
     * @param index The index of the song to be selected. */
    fun changeSong(index: Int) {
        mutableState.value = uiState.value.copyWith(
            selectedIndex = index,
            furigana = listOf(),
            selectedJapLines = getJapLines(index),
            selectedTransLines = getTransLines(index),
            loadingFurigana = false
        )
    }

    /** Makes a network call to retrieve furigana (kanji readings) for the currently selected
     * song. These are not persisted, but just kept in memory until a different song is selected. */
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

    /** Splits the japanese text of the specified song into an array of lines.
     * @param index The index of the song whose japanese text is to be split. */
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

    /** Splits the translated text of the specified song into an array of lines.
     * @param index The index of the song whose translation is to be split. */
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

    /** An object representing the state of Karaoke UI.
     * @param email The email of the current logged in user. An empty string if no user is logged in
     * yet.
     * @param selectedIndex The currently selected song's index. 0 by default.
     * @param selectedJapLines A list of strings representing the japanese text's lines.
     * @param selectedTransLines A list of strings representing the translated text's lines.
     * @param songs A list of [Song] objects representing the current user's saved songs.
     * @param furigana The japanese text subdivided in a list of words. Each element of the outer
     * list is a line. Then each line contains a list of words. Each word can contain one element
     * if the word is written in hiragana or katakana already, or two elements if the work contains
     * kanji. In the latter case, the firs element is the original writing, and the second is the
     * reading written in hiragana.
     * @param loadingFurigana A bool representing if a call for retrieving furigana is currently
     * running. */
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