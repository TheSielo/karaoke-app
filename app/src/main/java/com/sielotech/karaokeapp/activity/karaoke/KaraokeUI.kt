package com.sielotech.karaokeapp.activity.karaoke

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sielotech.karaokeapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


internal object KaraokeUI {

    @Composable
    fun KaraokeScreen(vm: KaraokeViewModel, onNavigateToNewSong: () -> Unit) {
        val scope = rememberCoroutineScope()
        val state by vm.uiState.collectAsState()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        ModalNavigationDrawer(
            drawerContent = {
                KaraokeDrawer(state, drawerState, vm, scope)
            },
            drawerState = drawerState,
        ) {
            Scaffold(
                Modifier.padding(PaddingValues(all = 16.dp)),
                floatingActionButton = {
                    KaraokeFAB(vm, scope)
                },
                topBar = {
                    KaraokeTopBar(state, drawerState, scope, onNavigateToNewSong)
                },
            ) { innerPadding ->
                if (state.songs.isEmpty()) {
                    NoSongsContent(innerPadding, onNavigateToNewSong)
                } else {
                    WithSongsContent(innerPadding, state)
                }
            }
        }
    }

    @Composable
    fun KaraokeDrawer(
        state: KaraokeViewModel.KaraokeUiState,
        drawerState: DrawerState,
        vm: KaraokeViewModel,
        scope: CoroutineScope
    ) {
        ModalDrawerSheet {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 32.dp)
                    .align(Alignment.Start)
            ) {
                Column {
                    Image(
                        painter = painterResource(R.drawable.icon_profile),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        contentDescription = "avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        state.email,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            HorizontalDivider()
            LazyColumn(modifier = Modifier.padding(all = 16.dp)) {
                itemsIndexed(state.songs) { index, song ->
                    NavigationDrawerItem(
                        label = { Text(text = song.title) },
                        selected = index == state.selectedIndex,
                        onClick = {
                            scope.launch {
                                vm.changeSong(index)
                                delay(300)
                                drawerState.close()
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun KaraokeFAB(
        vm: KaraokeViewModel,
        scope: CoroutineScope
    ) {
        ExtendedFloatingActionButton(
            onClick = {
                scope.launch {
                    vm.getFurigana()
                }
            },
            icon = { Icon(Icons.Filled.Add, "Add song") },
            text = { Text(text = "ふりがな", fontWeight = FontWeight.Bold) }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun KaraokeTopBar(
        state: KaraokeViewModel.KaraokeUiState,
        drawerState: DrawerState,
        scope: CoroutineScope,
        onNavigateToNewSong: () -> Unit
    ) {
        TopAppBar(
            title = {
                val title = if (state.songs.isEmpty()) {
                    "Furigana Lyrics Maker"
                } else {
                    state.songs[state.selectedIndex].title
                }
                Text(title)
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
                    })
                )
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
    }

    @Composable
    fun NoSongsContent(innerPadding: PaddingValues, onNavigateToNewSong: () -> Unit) {
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Add your first song to display the lyrics!",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onNavigateToNewSong,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            ) {
                Text(text = "Add a song")
            }
        }
    }

    @Composable
    fun WithSongsContent(innerPadding: PaddingValues, state: KaraokeViewModel.KaraokeUiState) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            state.selectedJapLines.forEachIndexed { index, line ->
                Column {
                    if (state.furigana.isEmpty()) {
                        Text(text = line, style = MaterialTheme.typography.headlineMedium)
                    } else {
                        FuriganaLine(state.furigana[index])
                    }
                    if (state.selectedTransLines.size > index) {
                        Text(
                            state.selectedTransLines[index],
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun FuriganaLine(line: List<List<String>>) {
        FlowRow {
            line.forEach { list ->
                Text(text = list[0], style = MaterialTheme.typography.headlineMedium)
                if (list.size == 2) {
                    Text(text = "(${list[1]}) ", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    }
}