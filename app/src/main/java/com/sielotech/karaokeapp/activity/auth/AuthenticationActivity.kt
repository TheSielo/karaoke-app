package com.sielotech.karaokeapp.activity.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.Firebase
import com.sielotech.karaokeapp.R
import com.sielotech.karaokeapp.activity.KActivity
import com.sielotech.karaokeapp.databinding.ActivityAuthenticationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
internal class AuthenticationActivity : KActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*b = ActivityAuthenticationBinding.inflate(layoutInflater)
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
                viewModel.authActivityUiState.collect { state ->
                    updateUI(state)
                }
            }
        }*/
    }

    private fun LoginContent() {

    }

    private fun updateUI(state: AuthenticationViewModel.AuthActivityUiState) {
        if (state is AuthenticationViewModel.AuthActivityUiState.Default) {
            //If the user is not authenticated, launch the authentication flow.
            if (!state.success) {
                finish()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.generic_error),
                    Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {}
}