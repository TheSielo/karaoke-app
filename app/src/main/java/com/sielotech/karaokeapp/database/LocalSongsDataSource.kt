package com.sielotech.karaokeapp.database

import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.database.entity.SongDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalSongsDataSource @Inject constructor(
    private val songDao: SongDao,
) {
    suspend fun addOrUpdate(song: Song) {
        songDao.insertOrUpdate(song)
    }

    fun getAllSongsFlow(): Flow<List<Song>> {
        return songDao.getSongsFlow()
    }

    suspend fun deleteSong(song: Song) {
        songDao.delete(song)
    }
}