package com.sielotech.karaokeapp.auth

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlinx.coroutines.*


class AuthenticationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun isUserLoggedIn(): Boolean {
        val currentUser = firebaseAuth.currentUser
        return currentUser != null
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