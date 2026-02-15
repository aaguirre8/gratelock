package com.gratelock.app.ui.screen.jar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gratelock.app.data.local.entity.JournalEntry
import com.gratelock.app.ui.theme.CardSurface
import com.gratelock.app.ui.theme.GrateLockTheme
import com.gratelock.app.util.DateUtils
import java.io.File

/**
 * Full-screen detail view for a single journal entry.
 *
 * Shows a typed entry's 3 gratitude items or a photo entry's full image
 * along with metadata (date, type, extracted text).
 *
 * @param viewModel The [EntryDetailViewModel] managing state.
 * @param onBack Callback to navigate back.
 */
@Composable
fun EntryDetailScreen(
    viewModel: EntryDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    EntryDetailContent(
        uiState = uiState,
        onBack = onBack,
    )
}

@Composable
private fun EntryDetailContent(
    uiState: EntryDetailUiState,
    onBack: () -> Unit = {},
) {
    when (uiState) {
        is EntryDetailUiState.Loading -> {
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

        is EntryDetailUiState.NotFound -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Entry not found",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onBack) {
                    Text("Go back")
                }
            }
        }

        is EntryDetailUiState.Success -> {
            val entry = uiState.entry
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
            ) {
                // Back button
                TextButton(onClick = onBack) {
                    Text(
                        text = "\u2190 Back to jar",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date
                Text(
                    text = DateUtils.toDisplayDate(entry.date),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Entry type badge
                Text(
                    text = if (entry.entryType == "typed") "Typed entry" else "Photo entry",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                )

                Spacer(modifier = Modifier.height(24.dp))

                when (entry.entryType) {
                    "typed" -> TypedEntryDetail(entry)
                    "photo" -> PhotoEntryDetail(entry)
                }

                // Keywords
                if (!entry.keywords.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Keywords",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = entry.keywords,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun TypedEntryDetail(entry: JournalEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardSurface)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        entry.gratitude1?.let { text ->
            GratitudeDetailItem(number = 1, text = text)
        }
        entry.gratitude2?.let { text ->
            GratitudeDetailItem(number = 2, text = text)
        }
        entry.gratitude3?.let { text ->
            GratitudeDetailItem(number = 3, text = text)
        }
    }
}

@Composable
private fun GratitudeDetailItem(number: Int, text: String) {
    Column {
        Text(
            text = "Gratitude #$number",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun PhotoEntryDetail(entry: JournalEntry) {
    Column {
        // Photo
        if (entry.photoPath != null) {
            AsyncImage(
                model = File(entry.photoPath),
                contentDescription = "Journal photo",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
            )
        }

        // Extracted text
        if (!entry.ocrText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Extracted text",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = entry.ocrText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardSurface)
                    .padding(16.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EntryDetailTypedPreview() {
    GrateLockTheme {
        EntryDetailContent(
            uiState = EntryDetailUiState.Success(
                entry = JournalEntry(
                    id = 1,
                    entryType = "typed",
                    gratitude1 = "Morning coffee with a friend at my favorite cafe",
                    gratitude2 = "Sunny weather on my walk home from work today",
                    gratitude3 = "A great conversation with my mom on the phone",
                    capturedAt = System.currentTimeMillis(),
                    date = "2026-02-15",
                    keywords = "coffee, friend, sunny, weather, mom",
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EntryDetailPhotoPreview() {
    GrateLockTheme {
        EntryDetailContent(
            uiState = EntryDetailUiState.Success(
                entry = JournalEntry(
                    id = 2,
                    entryType = "photo",
                    photoPath = "/data/photos/journal_1.jpg",
                    ocrText = "I am grateful for the sunshine today and my wonderful family",
                    capturedAt = System.currentTimeMillis(),
                    date = "2026-02-14",
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EntryDetailNotFoundPreview() {
    GrateLockTheme {
        EntryDetailContent(uiState = EntryDetailUiState.NotFound)
    }
}
