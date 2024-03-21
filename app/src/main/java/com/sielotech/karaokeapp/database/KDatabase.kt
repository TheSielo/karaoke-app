package com.sielotech.karaokeapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sielotech.karaokeapp.database.dao.Song
import com.sielotech.karaokeapp.database.entity.SongDao

@Database(entities = [Song::class], version = 5)
abstract class KDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}
