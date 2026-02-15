package com.gratelock.app.ui.screen.jar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.gratelock.app.data.local.entity.JournalEntry
import com.gratelock.app.ui.component.GratitudeCard
import com.gratelock.app.ui.component.PhotoThumbnail
import com.gratelock.app.ui.component.PhotoThumbnailPlaceholder
import com.gratelock.app.ui.theme.GrateLockTheme
import com.gratelock.app.util.DateUtils

/**
 * Jar screen showing a scrollable list of all journal entries.
 *
 * Typed entries appear as [GratitudeCard]s, photo entries as [PhotoThumbnail]s.
 * Tapping an entry navigates to a full-screen detail view.
 *
 * @param viewModel The [JarViewModel] managing state.
 * @param onEntryClick Callback when an entry is tapped, receives the entry ID.
 */
@Composable
fun JarScreen(
    viewModel: JarViewModel = hiltViewModel(),
    onEntryClick: (Long) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    JarScreenContent(
        uiState = uiState,
        onEntryClick = onEntryClick,
    )
}

@Composable
private fun JarScreenContent(
    uiState: JarUiState,
    onEntryClick: (Long) -> Unit = {},
) {
    when (uiState) {
        is JarUiState.Loading -> {
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

        is JarUiState.Success -> {
            if (uiState.entries.isEmpty()) {
                EmptyJarContent()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Text(
                            text = "Your Gratitude Jar",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }

                    items(
                        items = uiState.entries,
                        key = { it.id },
                    ) { entry ->
                        EntryListItem(
                            entry = entry,
                            onClick = { onEntryClick(entry.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EntryListItem(
    entry: JournalEntry,
    onClick: () -> Unit,
) {
    val displayDate = DateUtils.toDisplayDate(entry.date)

    when (entry.entryType) {
        "typed" -> {
            GratitudeCard(
                date = displayDate,
                gratitude1 = entry.gratitude1 ?: "",
                gratitude2 = entry.gratitude2,
                gratitude3 = entry.gratitude3,
                onClick = onClick,
            )
        }

        "photo" -> {
            val photoPath = entry.photoPath
            if (photoPath != null) {
                PhotoThumbnail(
                    photoPath = photoPath,
                    date = DateUtils.toShortDisplayDate(entry.date),
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                PhotoThumbnailPlaceholder(
                    date = DateUtils.toShortDisplayDate(entry.date),
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun EmptyJarContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "\uD83C\uDF6F",
            style = MaterialTheme.typography.displayLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your jar is empty",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Journal your first gratitude entry and it will appear here.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun JarScreenLoadingPreview() {
    GrateLockTheme {
        JarScreenContent(uiState = JarUiState.Loading)
    }
}

@Preview(showBackground = true)
@Composable
private fun JarScreenEmptyPreview() {
    GrateLockTheme {
        JarScreenContent(uiState = JarUiState.Success(entries = emptyList()))
    }
}

@Preview(showBackground = true)
@Composable
private fun JarScreenWithEntriesPreview() {
    GrateLockTheme {
        JarScreenContent(
            uiState = JarUiState.Success(
                entries = listOf(
                    JournalEntry(
                        id = 1,
                        entryType = "typed",
                        gratitude1 = "Morning coffee with a friend",
                        gratitude2 = "Sunny weather on my walk home",
                        gratitude3 = "A great conversation with my mom",
                        capturedAt = System.currentTimeMillis(),
                        date = "2026-02-15",
                    ),
                    JournalEntry(
                        id = 2,
                        entryType = "photo",
                        photoPath = "/data/photos/journal_1.jpg",
                        capturedAt = System.currentTimeMillis() - 86400000,
                        date = "2026-02-14",
                    ),
                ),
            ),
        )
    }
}
