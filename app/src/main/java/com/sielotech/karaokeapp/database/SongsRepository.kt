package com.sielotech.karaokeapp.database

import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.database.entity.SongDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Entry point for all operations involving creating, retrieving, updating and deleting songs.
 * @param songDao A [SongDao] singleton that's provided by Hilt through DI.
 */
class SongsRepository @Inject constructor(
    private val localSongsDataSource: LocalSongsDataSource,
    private val remoteSongsDataSource: RemoteSongsDataSource,
) {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    init {
        loadRemoteSongs()
    }

    private fun loadRemoteSongs() {
        scope.launch {
            remoteSongsDataSource.remoteSongsFlow.collect {songs ->
                for (song in songs) {
                    localSongsDataSource.addOrUpdate(song)
                }
            }
        }
    }

    suspend fun addOrUpdateSong(song: Song) {
        localSongsDataSource.addOrUpdate(song)
        remoteSongsDataSource.addOrUpdate(song)
    }

    fun getAllSongsFlow(): Flow<List<Song>> {
        return localSongsDataSource.getAllSongsFlow()
    }

    suspend fun deleteSong(song: Song) {
        localSongsDataSource.deleteSong(song)
    }
}