package com.sielotech.karaokeapp.activity.karaoke

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sielotech.karaokeapp.api.FuriganaRepository
import com.sielotech.karaokeapp.database.SongsRepository
import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.preferences.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.isA

@OptIn(ExperimentalCoroutinesApi::class)
class KaraokeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: KaraokeViewModel

    @Mock
    private lateinit var songsRepository: SongsRepository

    @Mock
    private lateinit var furiganaRepository: FuriganaRepository

    @Mock
    private lateinit var preferencesRepository: PreferencesRepository

    private lateinit var songsFlow: MutableStateFlow<List<Song>>

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        viewModel = KaraokeViewModel(songsRepository, furiganaRepository, preferencesRepository)

        // Mock userEmail flow to emit a default email an simulate a logged in user
        `when`(preferencesRepository.userEmail).thenReturn(flowOf("test@example.com"))

        songsFlow = MutableStateFlow(emptyList())

        // Mock the getAllSongsFlow to return our test list of songs
        `when`(songsRepository.getAllSongsFlow()).thenReturn(songsFlow)

        runBlocking { //deleteSong() is a suspend function
            // Mock the deleteSong method to do nothing
            `when`(songsRepository.deleteSong(isA<Song>())).thenReturn(Unit)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `deleteSong should delete the selected song and update state`() = runTest {
        //Create a fake song and add it to the flow
        val song = Song(1, "8d9623db-b28b-4479-ac24-27d522e04487", "Title",
            "JapaneseText", "TranslatedText", "")
        songsFlow.value = listOf(song)

        //This triggers the state update and monitoring
        viewModel.initialize()

        // Wait for the state to be updated
        advanceUntilIdle()

        viewModel.deleteSong()
        //Update the flow with an empty list, because we deleted the only song present
        songsFlow.value = emptyList()

        advanceUntilIdle()

        verify(songsRepository).deleteSong(song)

        // Assert that the state was updated to reflect the deletion
        val state = viewModel.uiState.value
        assert(state.songs.isEmpty())
        assert(state.selectedIndex == 0)
    }

    @Test
    fun `getFurigana should update furigana in state`() = runTest {
    }
}
