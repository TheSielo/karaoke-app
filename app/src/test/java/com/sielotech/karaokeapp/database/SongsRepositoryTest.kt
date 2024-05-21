package com.sielotech.karaokeapp.database

import com.sielotech.karaokeapp.database.dao.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SongsRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Mock
    private lateinit var localSongsDataSource: LocalSongsDataSource
    @Mock
    private lateinit var remoteSongsDataSource: RemoteSongsDataSource

    private lateinit var songsRepository: SongsRepository
    private lateinit var songsFlow: MutableStateFlow<List<Song>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        //backgroundScope is used because the repository will start a non-terminating coroutine
        songsRepository = SongsRepository(localSongsDataSource, remoteSongsDataSource, testScope.backgroundScope)
        songsFlow = MutableStateFlow(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadRemoteSongs should add or update songs in localSongsDataSource`() = testScope.runTest {
        /* Create some fake songs and add them to the flow */
        val song1 = mock<Song>()
        val song2 = mock<Song>()
        songsFlow.value = listOf(song1, song2)

        //Mock remoteSongsFlow to return our fake flow
        whenever(remoteSongsDataSource.remoteSongsFlow).thenReturn(songsFlow)

        //This triggers the flow collection
        songsRepository.initialize()

        //Wait for the repository to collect the flow
        advanceTimeBy(60000)

        verify(localSongsDataSource).addOrUpdate(song1)
        verify(localSongsDataSource).addOrUpdate(song2)
    }

    @Test
    fun `addOrUpdateSong should call local an remote data source`() = testScope.runTest {
        /* Create some fake songs */
        val song1 = mock<Song>()
        val song2 = mock<Song>()

        songsRepository.addOrUpdateSong(song1)
        songsRepository.addOrUpdateSong(song2)

        //Wait for the repository to finish adding the songs
        advanceUntilIdle()

        verify(localSongsDataSource).addOrUpdate(song1)
        verify(remoteSongsDataSource).addOrUpdate(song1)
        verify(localSongsDataSource).addOrUpdate(song2)
        verify(remoteSongsDataSource).addOrUpdate(song2)
    }

    @Test
    fun `getAllSongsFlow should return a flow containing a list of songs`() = testScope.runTest {
        /* Create some fake songs and add them to the flow */
        val song1 = mock<Song>()
        val song2 = mock<Song>()
        songsFlow.value = listOf(song1, song2)

        //Mock remoteSongsFlow to return our fake flow
        whenever(localSongsDataSource.getAllSongsFlow()).thenReturn(songsFlow)

        val flow = localSongsDataSource.getAllSongsFlow()
        assertEquals(songsFlow.value, flow.first())
    }

    @Test
    fun `deleteSong should call deleteSong on localSongsDataSource`() = testScope.runTest {
        val song = mock<Song>()
        songsRepository.deleteSong(song)
        //Wait for the repository to finish deleting the song
        advanceUntilIdle()

        verify(localSongsDataSource).deleteSong(song)
    }
}