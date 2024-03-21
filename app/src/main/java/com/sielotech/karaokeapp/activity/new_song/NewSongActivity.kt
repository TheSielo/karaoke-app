package com.sielotech.karaokeapp.activity.new_song

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sielotech.karaokeapp.activity.KActivity
import com.sielotech.karaokeapp.databinding.ActivityNewSongBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewSongActivity: KActivity() {

    private lateinit var b: ActivityNewSongBinding
    private val viewModel: NewSongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityNewSongBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.backButton.setOnClickListener {
            viewModel.backPressed()
        }
        b.nextButton.setOnClickListener {
            viewModel.nextPressed(
                b.japaneseEditText.text.toString().trim(),
                b.transEditText.text.toString().trim(),
                b.titleEditText.text.toString().trim(),
                b.urlEditText.text.toString().trim(),
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newSongActivityUiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: NewSongViewModel.NewSongActivityUiState) {
        if (state is NewSongViewModel.NewSongActivityUiState.Default) {
            when(state.step) {
                NewSongViewModel.CurrentStep.Japanese -> showGroups(true, false, false, false)
                NewSongViewModel.CurrentStep.Translation -> showGroups(false, true, false, true)
                NewSongViewModel.CurrentStep.Url -> showGroups(false, false, true, true)
                NewSongViewModel.CurrentStep.Completed -> finish()
            }
        }
    }

    private fun showGroups(japGroup: Boolean, transGroup: Boolean, urlGroup: Boolean, backButton: Boolean) {
        b.japGroup.visibility = if (japGroup) View.VISIBLE else View.GONE
        b.transGroup.visibility = if (transGroup) View.VISIBLE else View.GONE
        b.titleUrlGroup.visibility = if (urlGroup) View.VISIBLE else View.GONE
        b.backButton.visibility = if (backButton) View.VISIBLE else View.GONE
    }
}