package com.gratelock.app.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gratelock.app.ui.theme.GrateLockTheme

/**
 * Home screen composable.
 *
 * Displays the user's current streak, Happiness Jar fill level,
 * and today's unlock status. This is a placeholder implementation
 * that will be fully built in Increment 7.
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
    HomeScreenContent(uiState = uiState, onNavigateToAppSelection = onNavigateToAppSelection)
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToAppSelection: () -> Unit = {},
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "GrateLock",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Gratitude first, scroll second",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = if (uiState.isUnlockedToday) "Unlocked for today" else "Journal to unlock",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (uiState.isUnlockedToday) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    },
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = onNavigateToAppSelection,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = "Manage blocked apps",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
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
