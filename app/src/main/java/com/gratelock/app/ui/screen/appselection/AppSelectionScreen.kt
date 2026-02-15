package com.gratelock.app.ui.screen.appselection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gratelock.app.domain.model.AppCategory
import com.gratelock.app.ui.component.AppListItem

/**
 * App Selection screen composable.
 *
 * Displays all installed apps grouped by category with search, category
 * quick-select, and individual toggles. The user selects which apps to block.
 *
 * @param viewModel The [AppSelectionViewModel] managing state.
 * @param onSaveComplete Callback when the user finishes saving selections.
 */
@Composable
fun AppSelectionScreen(
    viewModel: AppSelectionViewModel = hiltViewModel(),
    onSaveComplete: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.saveComplete.collect {
            onSaveComplete()
        }
    }

    AppSelectionContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun AppSelectionContent(
    uiState: AppSelectionUiState,
    onEvent: (AppSelectionEvent) -> Unit,
) {
    when (uiState) {
        is AppSelectionUiState.Loading -> {
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

        is AppSelectionUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
        }

        is AppSelectionUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                // Header
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = "Choose apps to block",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "These apps will be locked until you journal each day",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search bar
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { onEvent(AppSelectionEvent.SearchChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Search apps...",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        ),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(
                            onClick = { onEvent(AppSelectionEvent.SelectAllSocialMedia) },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            Text(
                                text = "Select All Social Media",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }

                // App list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 8.dp),
                ) {
                    uiState.categories.forEach { category ->
                        val appsInCategory = uiState.apps.filter { it.category == category }
                        if (appsInCategory.isNotEmpty()) {
                            item(key = "header_${category.name}") {
                                CategoryHeader(category = category)
                            }
                            items(
                                items = appsInCategory,
                                key = { it.packageName },
                            ) { app ->
                                AppListItem(
                                    appName = app.appName,
                                    categoryName = app.category.displayName,
                                    icon = app.icon,
                                    isBlocked = app.isBlocked,
                                    onToggle = { isBlocked ->
                                        onEvent(
                                            AppSelectionEvent.ToggleApp(app.packageName, isBlocked)
                                        )
                                    },
                                )
                            }
                        }
                    }
                }

                // Bottom save bar
                SaveBar(
                    blockedCount = uiState.blockedCount,
                    onSave = { onEvent(AppSelectionEvent.Save) },
                )
            }
        }
    }
}

/**
 * Category section header in the app list.
 *
 * @param category The [AppCategory] to display.
 */
@Composable
private fun CategoryHeader(category: AppCategory) {
    Text(
        text = category.displayName,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
    )
}

/**
 * Bottom bar showing the blocked count and save button.
 *
 * @param blockedCount Number of apps currently selected for blocking.
 * @param onSave Callback when the save button is tapped.
 */
@Composable
private fun SaveBar(
    blockedCount: Int,
    onSave: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$blockedCount app${if (blockedCount != 1) "s" else ""} selected",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
