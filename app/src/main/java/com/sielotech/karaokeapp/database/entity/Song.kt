package com.sielotech.karaokeapp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** The entity representing a song in the Room database.
 * @param id By default it is set to 0, so that Room will auto-increment it. Otherwise the
 * specified uid will be used.
 * @param uuid An UUID used to identify the song outside the local database.
 * @param title The title of this song.
 * @param japaneseText The Japanese lyrics of this song.
 * @param translatedText The lyrics translated in a chosen language.
 * @param url The url of a YouTube video to play.
 */
@Entity(indices = [Index(value = ["uuid"], unique = true)])
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "uuid") val uuid: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "japanese_text") val japaneseText: String,
    @ColumnInfo(name = "translated_text") val translatedText: String,
    @ColumnInfo(name = "url") val url: String,
)
