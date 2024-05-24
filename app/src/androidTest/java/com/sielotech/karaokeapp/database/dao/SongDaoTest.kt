package com.sielotech.karaokeapp.database.dao

import androidx.test.filters.SmallTest
import com.sielotech.karaokeapp.database.KDatabase
import com.sielotech.karaokeapp.database.entity.Song
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidTest
@SmallTest
class SongDaoTest {

    /* Enable Hilt injection */
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var songDao: SongDao

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun songDao_should_write_and_read_a_song() = runBlocking {
        /* Create a fake song */
        val id = 1
        val uuid = "8d9623db-b28b-4479-ac24-27d522e04487"
        val title = "Title 1"
        val jText = "Japanese text 1"
        val tText = "Translated text 1"
        val url = "https://example1.com"
        val song = Song(id, uuid, title, jText, tText, url)

        /* Add it to the db */
        songDao.insertOrUpdate(song)
        val songs = songDao.getSongsFlow().first()

        /* Verify that all properties were stored and retrieved correctly */
        assert(songs.isNotEmpty())
        assert(songs[0].id == 1)
        assert(songs[0].uuid == uuid)
        assert(songs[0].title == title)
        assert(songs[0].japaneseText == jText)
        assert(songs[0].translatedText == tText)
        assert(songs[0].url == url)
    }

    @Test
    fun songDao_should_delete_a_song() = runBlocking {
        /* Create a fake song and add it to the db */
        val song = Song(1, "test-uuid", "Title", "Text", "Text", "Url")
        songDao.insertOrUpdate(song)

        /* Verify that the song was added correctly to the db */
        val flow = songDao.getSongsFlow()
        assert(flow.first().isNotEmpty())

        /* Delete the song and verify that is was delete correctly */
        songDao.delete(song)
        assert(flow.first().isEmpty())
    }
}