package com.sielotech.karaokeapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sielotech.karaokeapp.database.entity.Song
import com.sielotech.karaokeapp.database.dao.SongDao

/** This is the class that represents the Room database for the entire application. It defines
 * the entities and the version of the database. */
@Database(entities = [Song::class], version = 5)
abstract class KDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}
