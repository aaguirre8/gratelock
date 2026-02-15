package com.gratelock.app.ui.screen.overlay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gratelock.app.ui.navigation.Screen
import com.gratelock.app.ui.screen.camera.CameraScreen
import com.gratelock.app.ui.screen.gratitudeform.GratitudeFormScreen
import com.gratelock.app.ui.theme.GrateLockTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Full-screen undismissable overlay activity shown when the user
 * opens a blocked app.
 *
 * Launched by [com.gratelock.app.service.BlockingAccessibilityService]
 * with [FLAG_ACTIVITY_NEW_TASK]. The activity has an empty taskAffinity
 * and is excluded from recents to prevent the user from dismissing it.
 *
 * Contains a mini NavHost to navigate between the overlay screen and
 * entry flows (Gratitude Form, Camera, etc.).
 */
@AndroidEntryPoint
class BlockingOverlayActivity : ComponentActivity() {

    private val viewModel: BlockingOverlayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Block back press — overlay cannot be dismissed without journaling
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Intentionally empty — user must journal or use an emergency unlock.
            }
        })

        // Pass the blocked package to the ViewModel via SavedStateHandle
        intent.getStringExtra(EXTRA_BLOCKED_PACKAGE)?.let { pkg ->
            intent.putExtra("blocked_package", pkg)
        }

        setContent {
            GrateLockTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val navController = rememberNavController()

                // Dismiss overlay when unlocked
                if (uiState is BlockingOverlayUiState.Unlocked) {
                    finish()
                    return@GrateLockTheme
                }

                NavHost(
                    navController = navController,
                    startDestination = ROUTE_OVERLAY,
                ) {
                    composable(ROUTE_OVERLAY) {
                        BlockingOverlayScreen(
                            uiState = uiState,
                            onEvent = { event ->
                                when (event) {
                                    is BlockingOverlayEvent.TypeEntry -> {
                                        navController.navigate(Screen.GratitudeForm.route)
                                    }
                                    is BlockingOverlayEvent.PhotoEntry -> {
                                        navController.navigate(Screen.Camera.route)
                                    }
                                    else -> viewModel.onEvent(event)
                                }
                            },
                        )
                    }

                    composable(Screen.GratitudeForm.route) {
                        GratitudeFormScreen(
                            onUnlocked = {
                                viewModel.markUnlocked()
                            },
                        )
                    }

                    composable(Screen.Camera.route) {
                        CameraScreen(
                            onUnlocked = {
                                viewModel.markUnlocked()
                            },
                        )
                    }
                }
            }
        }
    }

    companion object {
        /** Intent extra key for the blocked app's package name. */
        const val EXTRA_BLOCKED_PACKAGE = "extra_blocked_package"

        private const val ROUTE_OVERLAY = "overlay"
    }
}
