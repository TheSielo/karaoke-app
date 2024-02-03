package com.sielotech.karaokeapp.database.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "japanese_text") val japaneseText: String?,
    @ColumnInfo(name = "translated_text") val translatedText: String?
)
