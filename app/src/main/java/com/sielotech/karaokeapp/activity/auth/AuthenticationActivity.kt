package com.sielotech.karaokeapp.activity.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.sielotech.karaokeapp.R
import com.sielotech.karaokeapp.activity.KActivity
import com.sielotech.karaokeapp.databinding.ActivityAuthenticationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
internal class AuthenticationActivity : KActivity() {
    private var _binding: ActivityAuthenticationBinding? = null
    private val b get() = _binding!!

    private val viewModel: AuthenticationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.registerButton.setOnClickListener {
            b.registrationGroup.visibility = View.VISIBLE
            b.loginGroup.visibility = View.GONE
        }

        b.regLoginInsteadButton.setOnClickListener {
            b.loginGroup.visibility = View.VISIBLE
            b.registrationGroup.visibility = View.GONE
        }

        b.regRegisterButton.setOnClickListener {
            val email = b.emailEditText.text.toString().trim()
            val password = b.passwordEditText.text.toString().trim()
            viewModel.registerUser(email, password)
        }

        b.signInButton.setOnClickListener {
            val email = b.emailEditText.text.toString().trim()
            val password = b.passwordEditText.text.toString().trim()
            viewModel.loginUser(email, password)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authActivityState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: AuthenticationViewModel.AuthActivityUiState) {
        if (state is AuthenticationViewModel.AuthActivityUiState.Default) {
            //If the user is authenticated, go back to MainActivity
            if (state.isLoggedIn) {
                finish()
            } else if (state.authenticationFailure) {
                Snackbar.make(
                    b.root,
                    getString(R.string.generic_error),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
    }
}