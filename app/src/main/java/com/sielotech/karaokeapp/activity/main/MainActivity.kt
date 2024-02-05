package com.sielotech.karaokeapp.activity.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sielotech.karaokeapp.R
import com.sielotech.karaokeapp.activity.KActivity
import com.sielotech.karaokeapp.activity.auth.AuthenticationActivity
import com.sielotech.karaokeapp.activity.new_song.NewSongActivity
import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class MainActivity : KActivity() {

    private lateinit var b: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationEvent.collect { event ->
                    if (event is MainActivityViewModel.NavigationEvent.NavigateToLogin) {
                        startActivity(Intent(this@MainActivity, AuthenticationActivity::class.java))
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mainActivityUiState.collect { state ->
                    updateUI(state)
                }
            }
        }

        b.topAppBar.apply {
            setNavigationOnClickListener { b.drawerLayout.open()}
            setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.menu_item_new_song -> {
                        startActivity(Intent(this@MainActivity, NewSongActivity::class.java))
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
        }

        val layoutManager = LinearLayoutManager(this)
        b.recyclerView.layoutManager = layoutManager
        b.playButton.setOnClickListener {
            viewModel.playButtonPressed()
        }
    }

    private fun updateUI(state: MainActivityViewModel.MainActivityUiState) {
        if (state is MainActivityViewModel.MainActivityUiState.Default) {
            b.topAppBar.title = state.currentSong.title

            b.navigationView.menu.clear()
            for (i in state.songs.indices) {
                val item = b.navigationView.menu.add(state.songs[i].title)
                item.setOnMenuItemClickListener {
                    viewModel.changeSong(i)
                    b.drawerLayout.close()
                    false
                }
            }

            updateKaraoke(state.currentSong, state.isPlaying)
        }
    }

    private fun updateKaraoke(currentSong: Song, isPlaying: Boolean) {
        val japLines = currentSong.japaneseText.split("\n")
        val transLines = currentSong.translatedText.split("\n")
        val lyrics = Lyrics(japLines, transLines)
        val adapter = KaraokeRecyclerViewAdapter(lyrics) {

        }
        b.recyclerView.adapter = adapter
        val icon = if(isPlaying) R.drawable.icon_pause else R.drawable.icon_play
        b.playButton.setImageDrawable(ContextCompat.getDrawable(this, icon))
    }
}