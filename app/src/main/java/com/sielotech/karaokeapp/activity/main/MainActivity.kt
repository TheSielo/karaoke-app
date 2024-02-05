package com.sielotech.karaokeapp.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sielotech.karaokeapp.activity.KActivity
import com.sielotech.karaokeapp.activity.auth.AuthenticationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class MainActivity : KActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    private fun updateUI(state: MainActivityViewModel.MainActivityUiState) {
        if(state is MainActivityViewModel.MainActivityUiState.Default) {
        }
    }
}