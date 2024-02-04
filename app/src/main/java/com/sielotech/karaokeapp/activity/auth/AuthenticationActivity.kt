package com.sielotech.karaokeapp.activity.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.sielotech.karaokeapp.R
import com.sielotech.karaokeapp.activity.KActivity
import com.sielotech.karaokeapp.databinding.ActivityAuthenticationBinding
import com.sielotech.karaokeapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : KActivity() {

    private lateinit var b: ActivityAuthenticationBinding
    private val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAuthenticationBinding.inflate(layoutInflater)
    }


    /** Launches to Login with Google authentication flow.
     *
     * While I don't like to leave all this boilerplate in an activity, its best place is here
     * because it's UI related stuff that doesn't really hold a state. Furthermore it's usually bad
     * to pass context references inside the view model.
     */
    private fun launchLoginFlow() {
        val signInLauncher = registerForActivityResult(
            FirebaseAuthUIActivityResultContract(),
        ) { res ->
            this.onSignInResult(res)
        }
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        // Create and launch sign-in intent
        val signInIntent =
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build()
        signInLauncher.launch(signInIntent)
    }

    /** The callback that handles the result from the Google authentication flow.
     * If the flow completed correctly, go back to MainActivity, otherwise show an error [Toast].
     * @param result An instance of [FirebaseAuthUIAuthenticationResult].
     */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            finish()
        } else {
            Toast.makeText(
                this,
                getString(R.string.generic_error),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}