package com.sielotech.karaokeapp.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.sielotech.karaokeapp.database.dao.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class RemoteSongsDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase,
) {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private val mutableRemoteSongsFlow = MutableStateFlow<List<Song>>(emptyList())
    val remoteSongsFlow = mutableRemoteSongsFlow.asStateFlow()

    init {
        waitForLogin()
    }

    private fun waitForLogin() {
        scope.launch {
            while(firebaseAuth.currentUser?.uid == null) {
                delay(1000)
            }
            val userId = firebaseAuth.currentUser?.uid
            if(userId != null) {
                val songRef = database.getReference("$userId/songs/")
                songRef.setValue(null)
            }
            getRemoteSongs()
        }
    }

    fun addOrUpdate(song: Song) {
        val userId = firebaseAuth.currentUser?.uid
        if(userId != null) {
            val songRef = database.getReference("$userId/songs/${song.uuid}")
            songRef.setValue(Gson().toJson(song))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getRemoteSongs() {
        val userId = firebaseAuth.currentUser?.uid
        if(userId != null) {
            val songsReg = database.getReference("$userId/songs")
            songsReg.addValueEventListener(object : ValueEventListener {
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
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.w("Failed to read value.", error.toException())
                }
            })
        }
    }
}