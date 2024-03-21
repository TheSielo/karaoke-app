package com.sielotech.karaokeapp.database

import android.content.Context
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Provides Hilt with the dependencies it needs for database related injection. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /** Provides the Room database.
     * @return A singleton instance of [KDatabase].
     */
    @Singleton
    @Provides
    fun provideKDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        KDatabase::class.java,
        "karaoke_app.db"
    )
        .fallbackToDestructiveMigration()
        .build()

    /** Provides the SongDao used by Room.
     * @return A singleton instance of [SongDao].
     */
    @Singleton
    @Provides
    fun provideSongDao(db: KDatabase) = db.songDao()

    @Singleton
    @Provides
    fun provideRealtimeDatabase() = Firebase.database
}