package com.sielotech.karaokeapp.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.sielotech.karaokeapp.database.entity.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/** This data source allows to add, modify and delete songs stored remotely.
 * @param firebaseAuth An instance of FirebaseAuth provided by [com.sielotech.karaokeapp.auth.AuthenticationModule].
 * @param database An instance of FirebaseDatabase provided by [DatabaseModule]
 * @param scope An instance of [CoroutineScope] provided by [com.sielotech.karaokeapp.hilt.CoroutineModule]
 * */
class RemoteSongsDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val scope: CoroutineScope,
) {
    private val mutableRemoteSongsFlow = MutableStateFlow<List<Song>>(emptyList())
    val remoteSongsFlow = mutableRemoteSongsFlow.asStateFlow()

    /** Calls waitForLogin(). */
    fun initialize() {
        waitForLogin()
    }

    /** If no user is logged in, it waits until one is logged in. Then calls getRemoteSongs(). */
    private fun waitForLogin() {
        scope.launch {
            while (firebaseAuth.currentUser?.uid == null) {
                delay(1000)
            }
            getRemoteSongs()
        }
    }

    /** Create a new song entry in the remote database if a song with the same UUID doesn't exist
     * or overwrites the song with the new data otherwise. */
    fun addOrUpdate(song: Song) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val songRef = database.getReference("$userId/songs/${song.uuid}")
            songRef.setValue(Gson().toJson(song))
        }
    }

    /** Creates a listener that returns snapshots of the songs stored on the remote database. It
     * returns a snapshot immediately, and then a snapshot everytime there's an update on the
     * server. */
    @Suppress("UNCHECKED_CAST")
    private fun getRemoteSongs() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val songsRef = database.getReference("$userId/songs")
            songsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        val songs: Map<String, Any> = dataSnapshot.value as Map<String, Any>
                        Timber.d("Value is: $songs")
                        val gson = Gson()
                        val deserializedSongs = arrayListOf<Song>()
                        for (song in songs.values) {
                            val deserializedSong = gson.fromJson(song as String, Song::class.java)
                            deserializedSongs.add(deserializedSong)
                        }
                        mutableRemoteSongsFlow.value = deserializedSongs
                    } catch (e: Exception) {
                        Timber.w("Deserialization error", e)
                        mutableRemoteSongsFlow.value = listOf()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.w("Failed to read value.", error.toException())
                }
            })
        }
    }
}