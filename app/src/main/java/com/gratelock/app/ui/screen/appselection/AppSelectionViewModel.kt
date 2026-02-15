package com.gratelock.app.ui.screen.appselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.local.entity.BlockedApp
import com.gratelock.app.data.repository.BlockedAppRepository
import com.gratelock.app.domain.model.AppCategory
import com.gratelock.app.domain.model.InstalledApp
import com.gratelock.app.domain.usecase.GetInstalledAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the App Selection screen.
 *
 * Loads installed apps from the device, allows the user to toggle blocking,
 * search/filter, and save selections to Room + SharedPreferences.
 *
 * @param getInstalledApps Use case to query installed apps.
 * @param blockedAppRepository Repository for persisting blocked app selections.
 * @param preferences SharedPreferences cache for the blocking service.
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val getInstalledApps: GetInstalledAppsUseCase,
    private val blockedAppRepository: BlockedAppRepository,
    private val preferences: GrateLockPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppSelectionUiState>(AppSelectionUiState.Loading)
    val uiState: StateFlow<AppSelectionUiState> = _uiState.asStateFlow()

    private val _saveComplete = MutableSharedFlow<Unit>()
    val saveComplete = _saveComplete.asSharedFlow()

    /** All apps before search filtering. */
    private var allApps: List<InstalledApp> = emptyList()

    init {
        loadApps()
    }

    /** @param event The [AppSelectionEvent] to process. */
    fun onEvent(event: AppSelectionEvent) {
        when (event) {
            is AppSelectionEvent.ToggleApp -> toggleApp(event.packageName, event.isBlocked)
            is AppSelectionEvent.SearchChanged -> updateSearch(event.query)
            is AppSelectionEvent.SelectAllSocialMedia -> selectAllSocialMedia()
            is AppSelectionEvent.Save -> saveSelections()
        }
    }

    private fun loadApps() {
        viewModelScope.launch {
            try {
                val blockedPackages = blockedAppRepository.getBlockedPackageNames().toSet()
                allApps = getInstalledApps(blockedPackages)
                updateUiState("")
            } catch (e: Exception) {
                _uiState.value = AppSelectionUiState.Error("Could not load installed apps")
            }
        }
    }

    private fun toggleApp(packageName: String, isBlocked: Boolean) {
        allApps = allApps.map { app ->
            if (app.packageName == packageName) app.copy(isBlocked = isBlocked) else app
        }
        val currentState = _uiState.value
        if (currentState is AppSelectionUiState.Success) {
            updateUiState(currentState.searchQuery)
        }
    }

    private fun updateSearch(query: String) {
        updateUiState(query)
    }

    private fun selectAllSocialMedia() {
        allApps = allApps.map { app ->
            if (app.category == AppCategory.SOCIAL_MEDIA) app.copy(isBlocked = true) else app
        }
        val currentState = _uiState.value
        if (currentState is AppSelectionUiState.Success) {
            updateUiState(currentState.searchQuery)
        }
    }

    private fun updateUiState(searchQuery: String) {
        val filtered = if (searchQuery.isBlank()) {
            allApps
        } else {
            allApps.filter {
                it.appName.contains(searchQuery, ignoreCase = true) ||
                    it.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
        val categories = filtered.map { it.category }.distinct().sortedBy { it.sortOrder }

        _uiState.value = AppSelectionUiState.Success(
            apps = filtered,
            searchQuery = searchQuery,
            blockedCount = allApps.count { it.isBlocked },
            categories = categories,
        )
    }

    private fun saveSelections() {
        viewModelScope.launch {
            val blockedApps = allApps.filter { it.isBlocked }.map { app ->
                BlockedApp(
                    packageName = app.packageName,
                    appName = app.appName,
                    isBlocked = true,
                    addedAt = System.currentTimeMillis(),
                )
            }

            // Persist to Room (source of truth)
            blockedAppRepository.deleteAll()
            blockedAppRepository.upsertAll(blockedApps)

            // Sync to SharedPreferences cache for the blocking service
            preferences.syncBlockedApps(blockedApps.map { it.packageName })

            _saveComplete.emit(Unit)
        }
    }
}
