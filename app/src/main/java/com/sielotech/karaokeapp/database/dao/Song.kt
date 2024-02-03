package com.sielotech.karaokeapp.database.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** The entity representing a song in the Room database.
 * @param uid By default it is set to 0, so that Room will auto-increment it. Otherwise the
 * specified uid will be used.
 * @param title The title of this song.
 * @param japaneseText The Japanese lyrics of this song.
 * @param translatedText The lyrics translated in a chosen language.
 */
@Entity
data class Song(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "japanese_text") val japaneseText: String?,
    @ColumnInfo(name = "translated_text") val translatedText: String?
)
