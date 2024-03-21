package com.sielotech.karaokeapp.activity.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

internal object KaraokeUI {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun KaraokeScreen(vm: KaraokeViewModel, onNavigateToLogin: () -> Unit, onNavigateToNewSong: () -> Unit,) {
        val scope = rememberCoroutineScope()
        val state by vm.uiState.collectAsState()
        LaunchedEffect("karaokeScreen") {
            vm.uiState.collect {

            }
        }

        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    Text("Drawer title", modifier = Modifier.padding(16.dp))
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text(text = "Drawer Item") },
                        selected = false,
                        onClick = { }
                    )

                }
            },
            drawerState = drawerState,
        ) {
            Scaffold(
                Modifier.padding(PaddingValues(all = 16.dp)),
                topBar = {
                    TopAppBar(
                        title = {
                            Text("Furigana Lyrics Maker")
                        },
                        navigationIcon = {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Open the menu",
                                modifier = Modifier.clickable(onClick = {
                                    scope.launch {
                                        drawerState.apply {
                                            if (isClosed) open() else close()
                                        }
                                    }
                                }))
                        },
                        actions = {
                            IconButton(onClick = { onNavigateToNewSong() }) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "New song",
                                )
                            }
                        },
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {}
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "")
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                }
            }
        }
    }
}