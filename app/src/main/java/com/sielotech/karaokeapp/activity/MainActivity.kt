package com.sielotech.karaokeapp.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sielotech.karaokeapp.activity.auth.AuthenticationUI.AuthenticationScreen
import com.sielotech.karaokeapp.activity.auth.AuthenticationViewModel
import com.sielotech.karaokeapp.activity.main.KaraokeUI.KaraokeScreen
import com.sielotech.karaokeapp.activity.main.KaraokeViewModel
import com.sielotech.karaokeapp.activity.new_song.NewSongViewModel
import com.sielotech.karaokeapp.activity.new_song.NewSongUI.NewSongScreen
import com.sielotech.karaokeapp.ui.theme.KaraokeAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class MainActivity : KActivity() {

    private val mainViewModel: KaraokeViewModel by viewModels()
    private val authViewModel: AuthenticationViewModel by viewModels()
    private val newSongViewModel: NewSongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationEvent.collect { event ->
                    if (event is MainActivityViewModel.NavigationEvent.NavigateToLogin) {
                        //startActivity(Intent(this@MainActivity, AuthenticationActivity::class.java))
                    }
                }
            }
        }*/

        setContent {
            KaraokeAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "karaoke") {
                        composable("karaoke") {
                            KaraokeScreen(
                                vm = hiltViewModel(),
                                onNavigateToLogin = { navController.navigate("login") },
                                onNavigateToNewSong = { navController.navigate("new_song") })
                        }
                        composable("login") {
                            AuthenticationScreen(
                                vm = hiltViewModel(),
                                navController = navController) }
                        composable("new_song") {
                            NewSongScreen(
                            vm = hiltViewModel(),
                            navController = navController
                        ) }
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
                KaraokeScreen(vm = viewModel(), onNavigateToLogin = {}, onNavigateToNewSong = {})
            }
        }
    }

}