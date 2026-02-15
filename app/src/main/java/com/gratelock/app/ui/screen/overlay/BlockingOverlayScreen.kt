package com.gratelock.app.ui.screen.overlay

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.gratelock.app.ui.theme.GrateLockTheme

/**
 * Blocking overlay screen content.
 *
 * Displays the blocked app icon and name, a warm message encouraging
 * the user to journal, two CTAs ("Type it" / "Photograph my journal"),
 * the current streak, and an emergency unlock link.
 *
 * @param uiState The current [BlockingOverlayUiState].
 * @param onEvent Callback for [BlockingOverlayEvent]s.
 */
@Composable
fun BlockingOverlayScreen(
    uiState: BlockingOverlayUiState,
    onEvent: (BlockingOverlayEvent) -> Unit,
) {
    when (uiState) {
        is BlockingOverlayUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        is BlockingOverlayUiState.Ready -> {
            OverlayReadyContent(uiState = uiState, onEvent = onEvent)
        }

        is BlockingOverlayUiState.Unlocked -> {
            // Activity handles dismissal via state observation
        }
    }
}

@Composable
private fun OverlayReadyContent(
    uiState: BlockingOverlayUiState.Ready,
    onEvent: (BlockingOverlayEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Blocked app icon
        if (uiState.blockedAppIcon != null) {
            Image(
                painter = rememberDrawablePainter(drawable = uiState.blockedAppIcon),
                contentDescription = "${uiState.blockedAppName} icon",
                modifier = Modifier.size(64.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Blocked app name
        Text(
            text = uiState.blockedAppName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Warm encouragement message
        Text(
            text = "Take a moment to practice gratitude first",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Streak display
        if (uiState.currentStreak > 0) {
            Text(
                text = "${uiState.currentStreak}-day streak",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // CTA: Type 3 things
        Button(
            onClick = { onEvent(BlockingOverlayEvent.TypeEntry) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = "Type 3 things I'm grateful for",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // CTA: Photograph journal
        OutlinedButton(
            onClick = { onEvent(BlockingOverlayEvent.PhotoEntry) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Photograph my journal",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Emergency unlock
        EmergencyUnlockSection(
            remaining = uiState.emergencyUnlocksRemaining,
            onUnlock = { onEvent(BlockingOverlayEvent.EmergencyUnlock) },
        )
    }
}

/**
 * Emergency unlock section at the bottom of the overlay.
 *
 * @param remaining Number of emergency unlocks left this week.
 * @param onUnlock Callback when the user taps to use an emergency unlock.
 */
@Composable
private fun EmergencyUnlockSection(
    remaining: Int,
    onUnlock: () -> Unit,
) {
    if (remaining > 0) {
        TextButton(onClick = onUnlock) {
            Text(
                text = "Emergency unlock ($remaining left this week)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        Text(
            text = "No emergency unlocks remaining this week",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OverlayReadyPreview() {
    GrateLockTheme {
        BlockingOverlayScreen(
            uiState = BlockingOverlayUiState.Ready(
                blockedAppName = "Instagram",
                blockedAppIcon = null,
                currentStreak = 7,
                emergencyUnlocksRemaining = 1,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OverlayNoStreakPreview() {
    GrateLockTheme {
        BlockingOverlayScreen(
            uiState = BlockingOverlayUiState.Ready(
                blockedAppName = "TikTok",
                blockedAppIcon = null,
                currentStreak = 0,
                emergencyUnlocksRemaining = 0,
            ),
            onEvent = {},
        )
    }
}
