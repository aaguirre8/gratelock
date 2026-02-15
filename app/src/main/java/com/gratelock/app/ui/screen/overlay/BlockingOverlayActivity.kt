package com.gratelock.app.ui.screen.overlay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
 * Currently both CTAs ("Type it" / "Photograph") temporarily unlock for
 * the day. Real entry flows will be connected in Increments 5 and 6.
 */
@AndroidEntryPoint
class BlockingOverlayActivity : ComponentActivity() {

    private val viewModel: BlockingOverlayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pass the blocked package to the ViewModel via SavedStateHandle
        intent.getStringExtra(EXTRA_BLOCKED_PACKAGE)?.let { pkg ->
            intent.putExtra("blocked_package", pkg)
        }

        setContent {
            GrateLockTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                // Dismiss overlay when unlocked
                if (uiState is BlockingOverlayUiState.Unlocked) {
                    finish()
                    return@GrateLockTheme
                }

                BlockingOverlayScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                )
            }
        }
    }

    @Deprecated("Back press should not dismiss the overlay")
    override fun onBackPressed() {
        // Intentionally empty — overlay cannot be dismissed with back button.
        // The user must journal or use an emergency unlock.
    }

    companion object {
        /** Intent extra key for the blocked app's package name. */
        const val EXTRA_BLOCKED_PACKAGE = "extra_blocked_package"
    }
}
