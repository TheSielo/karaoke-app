package com.sielotech.karaokeapp.database

import android.content.Context
import androidx.room.Room
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
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
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
}