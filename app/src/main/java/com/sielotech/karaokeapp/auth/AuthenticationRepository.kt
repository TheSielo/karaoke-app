package com.sielotech.karaokeapp.auth

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import javax.inject.Inject

class AuthenticationRepository @Inject constructor() {
    private val firebaseAuth = Firebase.auth

    fun isUserLoggedIn(): Boolean {
        val currentUser = firebaseAuth.currentUser
        return currentUser != null
    }
}