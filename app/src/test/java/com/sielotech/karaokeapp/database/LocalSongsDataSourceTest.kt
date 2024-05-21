package com.sielotech.karaokeapp.database

import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.database.entity.SongDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
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
        val song = mock<Song>()
        localSongsDataSource.addOrUpdate(song)
        verify(songDao).insertOrUpdate(song)
    }

    @Test
    fun `getAllSongsFlow should return a list of songs`() = runTest {

    }

    @Test
    fun `deleteSong should call deleteSong on songDao`() = runTest {
        val song = mock<Song>()
        localSongsDataSource.deleteSong(song)
        verify(songDao).delete(song)
    }
}