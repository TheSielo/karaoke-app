package com.sielotech.karaokeapp.activity.auth

import androidx.lifecycle.ViewModel
import com.sielotech.karaokeapp.auth.AuthenticationRepository
import javax.inject.Inject

class AuthenticationViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
): ViewModel() {

}