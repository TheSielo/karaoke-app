package com.sielotech.karaokeapp.activity.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sielotech.karaokeapp.auth.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AuthenticationViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
): ViewModel() {

    private val mutableState = MutableStateFlow<AuthActivityUiState>(AuthActivityUiState.Loading)
    val authActivityUiState = mutableState.asStateFlow()

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            val success = authenticationRepository.registerUser(email, password)
            mutableState.value = AuthActivityUiState.Default(success = success)
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val success = authenticationRepository.loginUser(email, password)
            mutableState.value = AuthActivityUiState.Default(success = success)
        }
    }

    internal sealed class AuthActivityUiState {
        data class Default(
            val success: Boolean
        ) : AuthActivityUiState()
        data object Loading: AuthActivityUiState()
    }
}