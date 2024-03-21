package com.sielotech.karaokeapp.activity.new_song

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
            val step = remember { mutableIntStateOf(1) }
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()
            ) {
                Column(Modifier.padding(padding)) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        value = textState.value,
                        onValueChange = { newText ->
                            textState.value = newText
                        },
                        label = { Text(text = "Paste here the japanese lyrics") }
                    )
                    Spacer(Modifier.height(16.dp))
                    Row {
                        Box(modifier = Modifier.weight(0.5f)) {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = step.intValue > 1,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                OutlinedButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { step.intValue-- }
                                ) {
                                    Text(text = "Previous")
                                }
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        Button(
                            modifier = Modifier.weight(0.5f),
                            onClick = { step.intValue++ }
                        ) {
                            Text(text = "Next")
                        }
                    }
                }
            }
        }
    }
}