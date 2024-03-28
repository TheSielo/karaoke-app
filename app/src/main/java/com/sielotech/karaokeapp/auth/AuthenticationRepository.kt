package com.sielotech.karaokeapp.auth

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber


class AuthenticationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    val userId = firebaseAuth.currentUser?.uid

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun userEmail(): String {
        return firebaseAuth.currentUser?.email?:""
    }

    suspend fun registerUser(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}