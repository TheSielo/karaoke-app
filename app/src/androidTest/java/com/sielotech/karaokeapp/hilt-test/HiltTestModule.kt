package com.sielotech.karaokeapp.hilt

import android.content.Context
import androidx.room.Room
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sielotech.karaokeapp.database.DatabaseModule
import com.sielotech.karaokeapp.database.KDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object HiltTestModule {

    @Provides
    fun provideInMemoryDb(@ApplicationContext context: Context): KDatabase {
        return Room.inMemoryDatabaseBuilder(context, KDatabase::class.java).build()
    }

    @Singleton
    @Provides
    fun provideSongDao(db: KDatabase) = db.songDao()

    @Singleton
    @Provides
    fun provideRealtimeDatabase() = Firebase.database
}