package com.sielotech.karaokeapp.database

import com.sielotech.karaokeapp.database.entity.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Entry point for all operations involving creating, retrieving, updating and deleting songs.
 * @param localSongsDataSource An instance of [LocalSongsDataSource] provided by Hilt.
 * @param remoteSongsDataSource An instance of [RemoteSongsDataSource] provided by Hilt.
 * @param scope An instance of [CoroutineScope] provided by [com.sielotech.karaokeapp.hilt.CoroutineModule]
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