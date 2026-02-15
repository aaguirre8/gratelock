package com.gratelock.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gratelock.app.ui.navigation.GrateLockNavHost
import com.gratelock.app.ui.theme.GrateLockTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point activity for GrateLock.
 *
 * Hosts the Compose NavHost and applies the app theme.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrateLockTheme {
                GrateLockNavHost()
            }
        }
    }
}
