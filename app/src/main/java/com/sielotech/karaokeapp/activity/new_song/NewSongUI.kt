package com.sielotech.karaokeapp.activity.new_song

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
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
        Scaffold(topBar = {
            TopAppBar(title = { Text("Add new song") }, navigationIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    modifier = Modifier.clickable(onClick = {
                        scope.launch {
                            navController.popBackStack()
                        }
                    })
                )
            })
        }) { innerPadding ->
            val padding = remember { PaddingValues(all = 16.dp) }
            val titleTextState = remember { mutableStateOf("") }
            val japTextState = remember { mutableStateOf("") }
            val transTextState = remember { mutableStateOf("") }
            val step = remember { mutableIntStateOf(1) }
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()
            ) {
                Column(Modifier.padding(padding)) {
                    Box(Modifier.weight(1f)) {
                        TopContent(step, titleTextState, japTextState, transTextState)
                    }
                    Spacer(Modifier.height(16.dp))
                    Buttons(vm, step, titleTextState, japTextState, transTextState)
                }
            }
        }
    }

    @Composable
    private fun TopContent(
        step: MutableIntState,
        titleTextState: MutableState<String>,
        japTextState: MutableState<String>,
        transTextState: MutableState<String>
    ) {
        AnimatedContent(
            targetState = step.intValue,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) {
            when (it) {
                1 -> {
                    OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                        value = titleTextState.value,
                        onValueChange = { newText ->
                            titleTextState.value = newText
                        },
                        singleLine = true,
                        label = { Text(text = "The song title") })
                }

                2 -> {
                    OutlinedTextField(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                        value = japTextState.value,
                        onValueChange = { newText ->
                            japTextState.value = newText
                        },
                        label = { Text(text = "Paste here the japanese lyrics") })
                }

                3 -> {
                    OutlinedTextField(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                        value = transTextState.value,
                        onValueChange = { newText ->
                            transTextState.value = newText
                        },
                        label = { Text(text = "Paste here the translation (optional)") })
                }
            }
        }
    }

    @Composable
    private fun Buttons(
        vm: NewSongViewModel,
        step: MutableIntState,
        titleTextState: MutableState<String>,
        japTextState: MutableState<String>,
        transTextState: MutableState<String>
    ) {
        Row {
            Box(modifier = Modifier.weight(0.5f)) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = step.intValue > 1, enter = fadeIn(), exit = fadeOut()
                ) {
                    OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                        if (step.intValue > 1) step.intValue--
                    }) {
                        Text(text = "Previous")
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Button(
                modifier = Modifier.weight(0.5f), onClick = {
                    if (step.intValue < 3) {
                        step.intValue++
                    } else {
                        vm.addOrUpdateSong(
                            titleTextState.value,
                            japTextState.value,
                            transTextState.value
                        )
                    }
                }, enabled = (step.intValue == 1 && titleTextState.value.isNotEmpty()) ||
                        step.intValue == 2 && japTextState.value.isNotEmpty() ||
                        step.intValue == 3
            ) {
                AnimatedContent(targetState = step.intValue == 3,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) {
                    if (it) {
                        Text(text = "Finish")
                    } else {
                        Text(text = "Next")
                    }
                }
            }
        }
    }
}