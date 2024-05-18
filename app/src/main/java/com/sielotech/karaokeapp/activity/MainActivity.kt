package com.sielotech.karaokeapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sielotech.karaokeapp.activity.auth.AuthenticationActivity
import com.sielotech.karaokeapp.activity.auth.AuthenticationViewModel
import com.sielotech.karaokeapp.activity.karaoke.KaraokeUI.KaraokeScreen
import com.sielotech.karaokeapp.activity.karaoke.KaraokeViewModel
import com.sielotech.karaokeapp.activity.new_song.NewSongUI.NewSongScreen
import com.sielotech.karaokeapp.activity.new_song.NewSongViewModel
import com.sielotech.karaokeapp.ui.theme.KaraokeAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class MainActivity : KActivity() {

    private val authViewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authActivityState.collect { state ->
                    if(state is AuthenticationViewModel.AuthActivityUiState.Default) {
                        //If the user is not authenticated, launch the authentication flow
                        if (!state.isLoggedIn) {
                            startActivity(
                                Intent(
                                    this@MainActivity, AuthenticationActivity::class.java
                                )
                            )
                        }
                    }
                }
            }
        }

        setContent {
            KaraokeAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "karaoke") {
                        composable("karaoke") {
                            KaraokeScreen(
                                vm = hiltViewModel<KaraokeViewModel>(),
                                onNavigateToNewSong = { navController.navigate("new_song") })
                        }
                        composable("new_song") {
                            NewSongScreen(
                                vm = hiltViewModel<NewSongViewModel>(),
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewApp() {
        KaraokeAppTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                KaraokeScreen(vm = viewModel(), onNavigateToNewSong = {})
            }
        }
    }
}