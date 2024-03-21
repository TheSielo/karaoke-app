package com.sielotech.karaokeapp.auth

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlinx.coroutines.*


class AuthenticationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    val userId = firebaseAuth.currentUser?.uid

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun registerUser(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
        result.isSuccessful
    }

    suspend fun loginUser(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password)
        result.isSuccessful
    }
}