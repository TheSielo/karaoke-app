package com.sielotech.karaokeapp.activity.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.sielotech.karaokeapp.database.dao.Song
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.addSong(Song(title = "ciao", japaneseText = "japanese", translatedText = "translation"))
        //val songs = viewModel.getSongs()
    }
}