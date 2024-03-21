package com.sielotech.karaokeapp.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

/** This is the abstract Activity class which every other Activity should implement. It contains
 * several useful logging and (soon) utility methods.
 */
abstract class KActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Lifecycle: onCreate - ${javaClass.simpleName}")
    }

    override fun onStart() {
        super.onStart()
        Timber.d("Lifecycle: onStart - ${javaClass.simpleName}")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("Lifecycle: onPause - ${javaClass.simpleName}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Lifecycle: onDestroy - ${javaClass.simpleName}")
    }
}