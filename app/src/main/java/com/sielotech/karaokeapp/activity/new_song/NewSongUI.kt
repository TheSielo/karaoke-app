package com.sielotech.karaokeapp.activity.new_song

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

internal object NewSongUI {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NewSongScreen(vm: NewSongViewModel, navController: NavController) {
        val scope = rememberCoroutineScope()
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Add new song") },
                    navigationIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            modifier = Modifier.clickable(onClick = {
                                scope.launch {
                                    navController.popBackStack()
                                }
                            })
                        )
                    }
                )
            }
        ) { innerPadding ->
            val padding = remember { PaddingValues(all = 16.dp) }
            val textState = remember { mutableStateOf("") }
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()
            ) {
                Column(Modifier.padding(padding)) {
                    Text(text = "Fill in the japanese lyrics:")
                    HorizontalDivider(Modifier.height(16.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = textState.value,
                        onValueChange = {
                            newText -> textState.value = newText
                        }
                    )
                }
                Row(
                    Modifier
                        .padding(padding)
                        .align(Alignment.BottomCenter)
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { }
                    ) {
                        Text(text = "Next")
                    }
                }
            }
        }
    }
}