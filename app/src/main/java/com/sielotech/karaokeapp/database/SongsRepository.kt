package com.sielotech.karaokeapp.database

import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.database.entity.SongDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

/** Entry point for all operations involving creating, retrieving, updating and deleting songs.
 * @param localSongsDataSource A [LocalSongsDataSource] instance provided by Hilt.
 * @param remoteSongsDataSource A [RemoteSongsDataSource] instance provided by Hilt.
 */
class SongsRepository @Inject constructor(
    private val localSongsDataSource: LocalSongsDataSource,
    private val remoteSongsDataSource: RemoteSongsDataSource,
    private val scope: CoroutineScope,
) {

    fun initialize() {
        remoteSongsDataSource.initialize()
        loadRemoteSongs()
    }

    private fun loadRemoteSongs() {
        scope.launch {
            remoteSongsDataSource.remoteSongsFlow.collect { songs ->
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