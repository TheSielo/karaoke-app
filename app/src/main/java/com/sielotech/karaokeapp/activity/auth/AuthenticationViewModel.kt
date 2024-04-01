package com.sielotech.karaokeapp.activity.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sielotech.karaokeapp.auth.AuthenticationRepository
import com.sielotech.karaokeapp.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AuthenticationViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val preferencesRepository: PreferencesRepository,
): ViewModel() {

    private val mutableState = MutableStateFlow<AuthActivityUiState>(AuthActivityUiState.Loading)
    val authActivityState = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.isLoggedIn.collect { isLoggedIn ->
                mutableState.value = AuthActivityUiState.Default(isLoggedIn = isLoggedIn, authenticationFailure = false)
            }
        }
    }

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            val success = authenticationRepository.registerUser(email, password)
            preferencesRepository.setIsLoggedIn(success)
            preferencesRepository.setUserEmail(authenticationRepository.userEmail())
            mutableState.value = AuthActivityUiState.Default(isLoggedIn = success, authenticationFailure = !success)
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val success = authenticationRepository.loginUser(email, password)
            preferencesRepository.setIsLoggedIn(success)
            preferencesRepository.setUserEmail(authenticationRepository.userEmail())
            mutableState.value = AuthActivityUiState.Default(isLoggedIn = success, authenticationFailure = !success)
        }
    }

    internal sealed class AuthActivityUiState {
        data class Default(
            val isLoggedIn: Boolean = false,
            val authenticationFailure: Boolean = false,
        ) : AuthActivityUiState()
        data object Loading: AuthActivityUiState()
    }
}