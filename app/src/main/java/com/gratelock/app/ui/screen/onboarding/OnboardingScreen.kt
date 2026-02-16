package com.gratelock.app.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gratelock.app.ui.component.PermissionCard
import com.gratelock.app.ui.theme.GrateLockTheme

/**
 * Onboarding screen composable.
 *
 * Guides the user through 3 pages: Welcome, How It Works, and Permissions.
 * After completing all pages, navigates to the App Selection screen,
 * then to Home.
 *
 * @param viewModel The [OnboardingViewModel] managing state.
 * @param onComplete Callback when onboarding is finished.
 * @param onNavigateToAppSelection Callback to navigate to app selection.
 */
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit = {},
    onNavigateToAppSelection: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Refresh permissions when screen is resumed (after returning from settings)
    LaunchedEffect(Unit) {
        viewModel.onEvent(OnboardingEvent.RefreshPermissions)
    }

    // Navigate away when onboarding is complete
    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onComplete()
        }
    }

    OnboardingContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToAppSelection = onNavigateToAppSelection,
    )
}

@Composable
private fun OnboardingContent(
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit = {},
    onNavigateToAppSelection: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Page indicators
        PageIndicator(
            currentPage = uiState.currentPage,
            totalPages = OnboardingUiState.PAGE_TOTAL,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Page content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when (uiState.currentPage) {
                OnboardingUiState.PAGE_WELCOME -> WelcomePage()
                OnboardingUiState.PAGE_HOW_IT_WORKS -> HowItWorksPage()
                OnboardingUiState.PAGE_PERMISSIONS -> PermissionsPage(
                    uiState = uiState,
                    onEvent = onEvent,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (uiState.currentPage > 0) {
                TextButton(
                    onClick = { onEvent(OnboardingEvent.PreviousPage) },
                ) {
                    Text(
                        text = "Back",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(64.dp))
            }

            when {
                uiState.currentPage < OnboardingUiState.PAGE_TOTAL - 1 -> {
                    Button(
                        onClick = { onEvent(OnboardingEvent.NextPage) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Text(
                            text = "Next",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }
                // Last page: "Get Started" button (requires permissions)
                else -> {
                    Button(
                        onClick = {
                            onEvent(OnboardingEvent.CompleteOnboarding)
                            onNavigateToAppSelection()
                        },
                        shape = RoundedCornerShape(12.dp),
                        enabled = uiState.allPermissionsGranted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Text(
                            text = "Get started",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PageIndicator(
    currentPage: Int,
    totalPages: Int,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (index == currentPage) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                    ),
            )
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "\uD83D\uDCDD",
            style = MaterialTheme.typography.displayLarge,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome to GrateLock",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Build a gratitude habit by journaling before scrolling. " +
                "Your distracting apps stay locked until you write.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HowItWorksPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "How it works",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        StepItem(
            number = "1",
            title = "Choose apps to block",
            description = "Pick the apps that distract you most — social media, games, etc.",
        )

        Spacer(modifier = Modifier.height(16.dp))

        StepItem(
            number = "2",
            title = "Journal your gratitude",
            description = "Each morning, type 3 things you're grateful for or photograph your journal.",
        )

        Spacer(modifier = Modifier.height(16.dp))

        StepItem(
            number = "3",
            title = "Apps unlock for the day",
            description = "After journaling, your blocked apps are unlocked until midnight.",
        )

        Spacer(modifier = Modifier.height(16.dp))

        StepItem(
            number = "4",
            title = "Build your streak",
            description = "Watch your jar fill and your streak grow as gratitude becomes a habit.",
        )
    }
}

@Composable
private fun StepItem(
    number: String,
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PermissionsPage(
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Permissions needed",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "GrateLock needs two permissions to block apps. " +
                "Your data stays on your device.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        PermissionCard(
            title = "Accessibility Service",
            description = "Detects when you open a blocked app so GrateLock can show the journaling screen.",
            isGranted = uiState.isAccessibilityEnabled,
            onGrant = { onEvent(OnboardingEvent.OpenAccessibilitySettings) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        PermissionCard(
            title = "Display over other apps",
            description = "Shows the journaling screen over blocked apps.",
            isGranted = uiState.isOverlayEnabled,
            onGrant = { onEvent(OnboardingEvent.OpenOverlaySettings) },
        )

        if (!uiState.allPermissionsGranted) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { onEvent(OnboardingEvent.RefreshPermissions) },
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "Check again",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingWelcomePreview() {
    GrateLockTheme {
        OnboardingContent(
            uiState = OnboardingUiState(currentPage = 0),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingHowItWorksPreview() {
    GrateLockTheme {
        OnboardingContent(
            uiState = OnboardingUiState(currentPage = 1),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPermissionsPreview() {
    GrateLockTheme {
        OnboardingContent(
            uiState = OnboardingUiState(
                currentPage = 2,
                isAccessibilityEnabled = true,
                isOverlayEnabled = false,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingAllGrantedPreview() {
    GrateLockTheme {
        OnboardingContent(
            uiState = OnboardingUiState(
                currentPage = 2,
                isAccessibilityEnabled = true,
                isOverlayEnabled = true,
            ),
        )
    }
}
