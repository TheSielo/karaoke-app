package com.sielotech.karaokeapp.activity.karaoke

import com.sielotech.karaokeapp.api.FuriganaRepository
import com.sielotech.karaokeapp.database.SongsRepository
import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.preferences.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.isA
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class KaraokeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var songsRepository: SongsRepository

    @Mock
    private lateinit var furiganaRepository: FuriganaRepository

    @Mock
    private lateinit var preferencesRepository: PreferencesRepository

    private lateinit var viewModel: KaraokeViewModel
    private lateinit var songsFlow: MutableStateFlow<List<Song>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        viewModel = KaraokeViewModel(songsRepository, furiganaRepository, preferencesRepository)

        // Mock userEmail flow to emit a default email an simulate a logged in user
        whenever(preferencesRepository.userEmail).thenReturn(flowOf("test@example.com"))

        songsFlow = MutableStateFlow(emptyList())
        // Mock the getAllSongsFlow to return our test list of songs
        whenever(songsRepository.getAllSongsFlow()).thenReturn(songsFlow)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `deleteSong should delete a song and update state (1 song list)`() = runTest {

        //Create a fake song and add it to the flow
        val song = Song(
            1, "8d9623db-b28b-4479-ac24-27d522e04487", "Title",
            "JapaneseText", "TranslatedText", ""
        )
        songsFlow.value = listOf(song)

        //This triggers the state update and monitoring
        viewModel.initialize()
        // Wait for the state to be updated
        advanceUntilIdle()

        viewModel.deleteSong()
        //Update the flow with an empty list, because we deleted the only song present
        songsFlow.value = emptyList()

        advanceUntilIdle()

        // Assert that the state was updated correctly
        val state = viewModel.uiState.value
        assert(state.songs.isEmpty())
        assert(state.selectedIndex == 0)

        verify(songsRepository).deleteSong(song)
    }

    @Test
    fun `changeSong should update selectedIndex, japaneseText and translatedText`() = runTest {
        //The fake song containing the above texts
        val song1 = Song(1, "8d9623db-b28b-4479-ac24-27d522e04487", "Title 1",
            "Japanese text 1", "Translated text 1", "Url 1"
        )
        val jText2 = "Japanese text 2"
        val tText2 = "Translated text 2"
        //The fake song containing the above texts
        val song2 = Song(2, "5fb687f7-0625-4e86-9946-9b1a7ec30cba", "Title 2",
            "$jText2\n$jText2", "$tText2\n$tText2", "Url 2"
        )
        //Update the songs flow
        songsFlow.value = listOf(song1, song2)

        //This triggers the state update and monitoring
        viewModel.initialize()
        // Wait for the state to be updated
        advanceUntilIdle()

        viewModel.changeSong(1)
        advanceUntilIdle()

        // Assert that the state was updated correctly
        val state = viewModel.uiState.value
        assert(state.selectedIndex == 1)
        assert(state.selectedJapLines == listOf(jText2, jText2))
        assert(state.selectedTransLines == listOf(tText2, tText2))
        assert(state.furigana.isEmpty())
        assert(!state.loadingFurigana)
    }

    @Test
    fun `getFurigana should update furigana`() = runTest {
        //The text whose furigana have to be returned
        val japaneseText = "日本はだいすき\n日本はだいすき\n\n日本はだいすき"
        val translatedText = "I love Japan\nI love Japan\n\n I love Japan"

        //The fake song containing the above texts
        val song = Song( 1, "8d9623db-b28b-4479-ac24-27d522e04487", "Title",
            japaneseText, translatedText, ""
        )
        /* This is what the ViewModel state should contain after elaborating the data from
        furiganaRepository. */
        val furiganaResult =
            listOf(
                listOf(listOf("日本", "にほん"), listOf("は"), listOf("だいすき")),
                listOf(listOf("日本", "にほん"), listOf("は"), listOf("だいすき")),
                listOf(listOf("日本", "にほん"), listOf("は"), listOf("だいすき")),
            )

        //The text sent to furiganaRepository should be cleansed of consecutive "\n" occurrences
        val cleanedText = "日本はだいすき\n日本はだいすき\n日本はだいすき"
        whenever(furiganaRepository.getFurigana(cleanedText)).thenReturn(
            listOf(
                listOf("日本", "にほん"), listOf("は"), listOf("だいすき"), listOf("\n"),
                listOf("日本", "にほん"), listOf("は"), listOf("だいすき"), listOf("\n"),
                listOf("日本", "にほん"), listOf("は"), listOf("だいすき"),
            )
        )

        //Create a fake song and add it to the flow
        songsFlow.value = listOf(song)

        //This triggers the state update and monitoring
        viewModel.initialize()
        // Wait for the state to be updated
        advanceUntilIdle()

        viewModel.getFurigana()
        advanceUntilIdle()

        //Assert that the state was updated correctly
        val state = viewModel.uiState.value
        assert(state.furigana == furiganaResult)
        assert(!state.loadingFurigana)

        verify(furiganaRepository).getFurigana(cleanedText)
    }
}
