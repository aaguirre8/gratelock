package com.gratelock.app.ui.screen.gratitudeform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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

/** Rotating placeholder prompts for each field. */
private val PLACEHOLDERS = listOf(
    listOf(
        "A person who made me smile today...",
        "Something that brought me joy...",
        "A small thing I often overlook...",
    ),
    listOf(
        "A moment that felt meaningful...",
        "Something beautiful I noticed...",
        "A skill or ability I appreciate...",
    ),
    listOf(
        "Something that made today better...",
        "A comfort I'm thankful for...",
        "A memory that warms my heart...",
    ),
)

/**
 * Gratitude Form screen composable.
 *
 * Displays 3 text fields for the user to type what they're grateful for,
 * with inline validation errors and a submit button. On success,
 * signals the parent to unlock apps.
 *
 * @param viewModel The [GratitudeFormViewModel] managing state.
 * @param onUnlocked Callback when the entry is saved and apps are unlocked.
 */
@Composable
fun GratitudeFormScreen(
    viewModel: GratitudeFormViewModel = hiltViewModel(),
    onUnlocked: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    GratitudeFormContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onUnlocked = onUnlocked,
    )
}

@Composable
private fun GratitudeFormContent(
    uiState: GratitudeFormUiState,
    onEvent: (GratitudeFormEvent) -> Unit = {},
    onUnlocked: () -> Unit = {},
) {
    when (uiState) {
        is GratitudeFormUiState.Loading -> {
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

        is GratitudeFormUiState.Ready -> {
            FormReadyContent(uiState = uiState, onEvent = onEvent)
        }

        is GratitudeFormUiState.Success -> {
            SuccessContent(streak = uiState.newStreak, onUnlocked = onUnlocked)
        }
    }
}

@Composable
private fun FormReadyContent(
    uiState: GratitudeFormUiState.Ready,
    onEvent: (GratitudeFormEvent) -> Unit,
) {
    val scrollState = rememberScrollState()
    val fields = listOf(uiState.field1, uiState.field2, uiState.field3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "What are you grateful for?",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Share 3 things — big or small",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        fields.forEachIndexed { index, text ->
            GratitudeTextField(
                index = index,
                text = text,
                error = uiState.fieldErrors[index],
                onTextChange = { onEvent(GratitudeFormEvent.FieldChanged(index, it)) },
            )
            if (index < 2) Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onEvent(GratitudeFormEvent.Submit) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSubmitting,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = "Unlock my apps",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun GratitudeTextField(
    index: Int,
    text: String,
    error: String?,
    onTextChange: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${index + 1}.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp),
        )

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = PLACEHOLDERS[index][0],
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            },
            isError = error != null,
            supportingText = if (error != null) {
                {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            } else {
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Text(
                            text = "${text.length}/200",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            ),
            minLines = 2,
            maxLines = 4,
        )
    }
}

@Composable
private fun SuccessContent(
    streak: Int,
    onUnlocked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Apps unlocked!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Thank you for practicing gratitude today.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        if (streak > 0) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$streak-day streak",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onUnlocked,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormReadyPreview() {
    GrateLockTheme {
        GratitudeFormContent(uiState = GratitudeFormUiState.Ready())
    }
}

@Preview(showBackground = true)
@Composable
private fun FormWithErrorsPreview() {
    GrateLockTheme {
        GratitudeFormContent(
            uiState = GratitudeFormUiState.Ready(
                field1 = "hi",
                fieldErrors = mapOf(
                    0 to "Could you share a bit more? At least a few words would be wonderful.",
                    1 to "Could you share a bit more? At least a few words would be wonderful.",
                    2 to "Could you share a bit more? At least a few words would be wonderful.",
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormSubmittingPreview() {
    GrateLockTheme {
        GratitudeFormContent(
            uiState = GratitudeFormUiState.Ready(
                field1 = "I'm grateful for my morning coffee",
                field2 = "The sunshine through the window was beautiful",
                field3 = "My friend called to check on me today",
                isSubmitting = true,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormSuccessPreview() {
    GrateLockTheme {
        GratitudeFormContent(uiState = GratitudeFormUiState.Success(newStreak = 7))
    }
}
