package com.sielotech.karaokeapp.database

import com.sielotech.karaokeapp.database.entity.Song
import com.sielotech.karaokeapp.database.dao.SongDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LocalSongsDataSourceTest {

    @Mock
    private lateinit var songDao: SongDao

    private lateinit var localSongsDataSource: LocalSongsDataSource
    private lateinit var songsFlow: MutableStateFlow<List<Song>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())

        localSongsDataSource = LocalSongsDataSource(songDao)
        songsFlow = MutableStateFlow(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addOrUpdate should call insertOrUpdate on songDao`() = runTest {
        /* Mock a song and verify that the method was called */
        val song = mock<Song>()
        localSongsDataSource.addOrUpdate(song)
        verify(songDao).insertOrUpdate(song)
    }

    @Test
    fun `getAllSongsFlow should return a flow with a list of songs`() = runTest {
        /* Mock some songs and add them to the flow */
        val song1 = mock<Song>()
        val song2 = mock<Song>()
        val song3 = mock<Song>()
        val expectedList = listOf(song1, song2, song3)
        songsFlow.value = expectedList

        //Mock songDao to return the fake list
        whenever(songDao.getSongsFlow()).thenReturn(songsFlow)

        /* Check that the flow contains what we expect */
        val songs = songDao.getSongsFlow().first()
        assert(songs == expectedList)
    }

    @Test
    fun `deleteSong should call deleteSong on songDao`() = runTest {
        /* Mock a song and verify that the method was called */
        val song = mock<Song>()
        localSongsDataSource.deleteSong(song)
        verify(songDao).delete(song)
    }
}