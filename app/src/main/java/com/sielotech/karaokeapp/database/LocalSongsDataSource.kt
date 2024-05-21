package com.sielotech.karaokeapp.database

import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.database.entity.SongDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** This data source allows to add, modify and delete songs stored locally. */
class LocalSongsDataSource @Inject constructor(
    private val songDao: SongDao,
) {
    /** Create a new song entry in the local database if a song with the same UUID doesn't exist
     * or overwrites the song with the new data otherwise. */
    suspend fun addOrUpdate(song: Song) {
        songDao.insertOrUpdate(song)
    }

    /** Returns a flow containing a list of all the songs currently present in the local database.
     * @return A list of all the songs present in the local database. */
    fun getAllSongsFlow(): Flow<List<Song>> {
        return songDao.getSongsFlow()
    }

    /** Deletes the specified song from the local database. */
    suspend fun deleteSong(song: Song) {
        songDao.delete(song)
    }
}