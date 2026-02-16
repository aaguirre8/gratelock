package com.gratelock.app.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gratelock.app.ui.component.HappinessJar
import com.gratelock.app.ui.component.StreakBadge
import com.gratelock.app.ui.component.TodayStatus
import com.gratelock.app.ui.theme.GrateLockTheme

/**
 * Home screen composable.
 *
 * Displays the user's current streak, Happiness Jar fill level,
 * and today's unlock status. Shows real data from Room stats.
 * Shows a banner if the accessibility service has been disabled.
 *
 * @param viewModel The [HomeViewModel] managing state.
 * @param onNavigateToAppSelection Callback to navigate to the App Selection screen.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAppSelection: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Refresh service status when screen is resumed (after returning from settings)
    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvent.RefreshServiceStatus)
    }

    HomeScreenContent(
        uiState = uiState,
        onNavigateToAppSelection = onNavigateToAppSelection,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToAppSelection: () -> Unit = {},
    onEvent: (HomeEvent) -> Unit = {},
) {
    when (uiState) {
        is HomeUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        is HomeUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "GrateLock",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Gratitude first, scroll second",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                // Accessibility service disabled banner
                if (!uiState.isAccessibilityServiceEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))

                    ServiceDisabledBanner(
                        onReEnable = { onEvent(HomeEvent.OpenAccessibilitySettings) },
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                TodayStatus(
                    isUnlockedToday = uiState.isUnlockedToday,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(24.dp))

                StreakBadge(
                    currentStreak = uiState.currentStreak,
                    longestStreak = uiState.longestStreak,
                )

                Spacer(modifier = Modifier.height(32.dp))

                HappinessJar(
                    fillPercent = uiState.jarFillPercent,
                    totalEntries = uiState.totalEntries,
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedButton(
                    onClick = onNavigateToAppSelection,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = "Manage blocked apps",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                // Debug button: only shown when unlocked, for testing
                if (uiState.isUnlockedToday) {
                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { onEvent(HomeEvent.DebugRelock) },
                    ) {
                        Text(
                            text = "Re-lock apps (debug)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ServiceDisabledBanner(
    onReEnable: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Blocking service is disabled",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "GrateLock can't block apps without the accessibility service. " +
                    "Tap below to re-enable it.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onReEnable,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
            ) {
                Text(
                    text = "Re-enable service",
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenLoadingPreview() {
    GrateLockTheme {
        HomeScreenContent(uiState = HomeUiState.Loading)
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenLockedPreview() {
    GrateLockTheme {
        HomeScreenContent(
            uiState = HomeUiState.Success(
                currentStreak = 0,
                isUnlockedToday = false,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenUnlockedPreview() {
    GrateLockTheme {
        HomeScreenContent(
            uiState = HomeUiState.Success(
                currentStreak = 12,
                longestStreak = 30,
                totalEntries = 47,
                jarFillPercent = 0.47f,
                isUnlockedToday = true,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenServiceDisabledPreview() {
    GrateLockTheme {
        HomeScreenContent(
            uiState = HomeUiState.Success(
                currentStreak = 5,
                isUnlockedToday = false,
                isAccessibilityServiceEnabled = false,
            ),
        )
    }
}
