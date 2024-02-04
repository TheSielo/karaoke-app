package com.sielotech.karaokeapp.database

import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.database.entity.SongDao
import javax.inject.Inject

/** Entry point for all operations involving creating, retrieving, updating and deleting songs.
 * @param songDao A [SongDao] singleton that's provided by Hilt through DI.
 */
class SongsRepository @Inject constructor(
    private val songDao: SongDao
) {
    suspend fun addSong(song: Song) {
        songDao.insert(song)
    }

    suspend fun getSongs(): List<Song> {
        return songDao.getAll()
    }
}