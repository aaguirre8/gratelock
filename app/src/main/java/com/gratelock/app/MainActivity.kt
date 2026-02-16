package com.gratelock.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.ui.navigation.GrateLockNavHost
import com.gratelock.app.ui.navigation.Screen
import com.gratelock.app.ui.theme.GrateLockTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main entry point activity for GrateLock.
 *
 * Hosts the Compose NavHost and applies the app theme.
 * Determines the start destination based on onboarding state.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferences: GrateLockPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = if (preferences.onboardingComplete) {
            Screen.Home.route
        } else {
            Screen.Onboarding.route
        }

        setContent {
            GrateLockTheme {
                GrateLockNavHost(startDestination = startDestination)
            }
        }
    }
}
