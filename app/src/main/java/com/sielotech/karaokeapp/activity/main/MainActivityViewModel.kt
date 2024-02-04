package com.sielotech.karaokeapp.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sielotech.karaokeapp.api.FuriganaApiHelper
import com.sielotech.karaokeapp.database.SongsRepository
import com.sielotech.karaokeapp.database.dao.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/** Manages the state of [MainActivity].
 * @param songsRepository A [SongsRepository] singleton provided by Hilt through DI.
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val songsRepository: SongsRepository
): ViewModel() {

    fun addSong(song: Song) {
        viewModelScope.launch {
            songsRepository.addSong(song)
            val result = FuriganaApiHelper.furiganaService.getFurigana(mapOf("text" to "日本語の車は大好きです"))
            Timber.d(result.toString())
        }
    }

    /*fun getSongs(): List<Song> {
        return songsRepository.getSongs()
    }*/
}