package com.sielotech.karaokeapp.database.entity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sielotech.karaokeapp.database.dao.Song
import kotlinx.coroutines.flow.Flow

/** The Room DAO relative to the [Song] entity. */
@Dao
interface SongDao {
    /** Return all the songs present in the song table. */
    @Query("SELECT * FROM song")
    fun getSongsFlow(): Flow<List<Song>>

    /** Insert a new song in the [song] table.
     * @param song An instance of the song to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(song: Song)

    /** Delete a song from the [song] table.
     * @param song The song to be deleted.
     */
    @Delete
    suspend fun delete(song: Song)
}
