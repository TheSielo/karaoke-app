package com.sielotech.karaokeapp

import android.content.Context
import androidx.room.Room
import com.sielotech.karaokeapp.database.KDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Contains methods that provides Hilt with the dependencies it needs for application-wide
 * injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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