package com.gratelock.app.ui.screen.appselection

import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.local.entity.BlockedApp
import com.gratelock.app.data.repository.BlockedAppRepository
import com.gratelock.app.domain.model.AppCategory
import com.gratelock.app.domain.model.InstalledApp
import com.gratelock.app.domain.usecase.GetInstalledAppsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AppSelectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getInstalledApps: GetInstalledAppsUseCase
    private lateinit var blockedAppRepository: BlockedAppRepository
    private lateinit var preferences: GrateLockPreferences

    private val fakeApps = listOf(
        InstalledApp("com.instagram.android", "Instagram", null, false, AppCategory.SOCIAL_MEDIA),
        InstalledApp("com.twitter.android", "Twitter", null, false, AppCategory.SOCIAL_MEDIA),
        InstalledApp("com.google.android.youtube", "YouTube", null, false, AppCategory.VIDEO),
        InstalledApp("com.example.other", "Other App", null, false, AppCategory.OTHER),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getInstalledApps = mock()
        blockedAppRepository = mock()
        preferences = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): AppSelectionViewModel {
        whenever(getInstalledApps.invoke(any())).thenReturn(fakeApps)

        return AppSelectionViewModel(
            getInstalledApps = getInstalledApps,
            blockedAppRepository = blockedAppRepository,
            preferences = preferences,
        )
    }

    @Test
    fun initialState_isLoading() = runTest {
        whenever(getInstalledApps.invoke(any())).thenReturn(fakeApps)
        whenever(blockedAppRepository.getBlockedPackageNames()).thenReturn(emptyList())

        val viewModel = AppSelectionViewModel(getInstalledApps, blockedAppRepository, preferences)

        // Before coroutine completes, state is Loading
        assertTrue(viewModel.uiState.value is AppSelectionUiState.Loading)
    }

    @Test
    fun afterLoad_stateIsSuccess() = runTest {
        whenever(blockedAppRepository.getBlockedPackageNames()).thenReturn(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AppSelectionUiState.Success)
        assertEquals(4, (state as AppSelectionUiState.Success).apps.size)
    }

    @Test
    fun afterLoad_categoriesAreSorted() = runTest {
        whenever(blockedAppRepository.getBlockedPackageNames()).thenReturn(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AppSelectionUiState.Success
        assertEquals(
            listOf(AppCategory.SOCIAL_MEDIA, AppCategory.VIDEO, AppCategory.OTHER),
            state.categories,
        )
    }

    @Test
    fun toggleApp_updatesBlockedState() = runTest {
        whenever(blockedAppRepository.getBlockedPackageNames()).thenReturn(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AppSelectionEvent.ToggleApp("com.instagram.android", true))

        val state = viewModel.uiState.value as AppSelectionUiState.Success
        val instagram = state.apps.find { it.packageName == "com.instagram.android" }
        assertTrue(instagram!!.isBlocked)
        assertEquals(1, state.blockedCount)
    }

    @Test
    fun toggleApp_canUnblock() = runTest {
        whenever(blockedAppRepository.getBlockedPackageNames()).thenReturn(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AppSelectionEvent.ToggleApp("com.instagram.android", true))
        viewModel.onEvent(AppSelectionEvent.ToggleApp("com.instagram.android", false))

        val state = viewModel.uiState.value as AppSelectionUiState.Success
        val instagram = state.apps.find { it.packageName == "com.instagram.android" }
        assertFalse(instagram!!.isBlocked)
        assertEquals(0, state.blockedCount)
    }

    @Test
    fun selectAllSocialMedia_blocksOnlySocialApps() = runTest {
        whenever(blockedAppRepository.getBlockedPackageNames()).thenReturn(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AppSelectionEvent.SelectAllSocialMedia)

        val state = viewModel.uiState.value as AppSelectionUiState.Success
        val socialApps = state.apps.filter { it.category == AppCategory.SOCIAL_MEDIA }
        val nonSocialApps = state.apps.filter { it.category != AppCategory.SOCIAL_MEDIA }

        assertTrue(socialApps.all { it.isBlocked })
        assertTrue(nonSocialApps.none { it.isBlocked })
        assertEquals(2, state.blockedCount)
    }

    @Test
    fun searchChanged_filtersApps() = runTest {
        whenever(blockedAppRepository.getBlockedPackageNames()).thenReturn(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AppSelectionEvent.SearchChanged("insta"))

        val state = viewModel.uiState.value as AppSelectionUiState.Success
        assertEquals(1, state.apps.size)
        assertEquals("com.instagram.android", state.apps[0].packageName)
    }

    @Test
    fun searchChanged_isCaseInsensitive() = runTest {
        whenever(blockedAppRepository.getBlockedPackageNames()).thenReturn(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AppSelectionEvent.SearchChanged("YOUTUBE"))

        val state = viewModel.uiState.value as AppSelectionUiState.Success
        assertEquals(1, state.apps.size)
        assertEquals("com.google.android.youtube", state.apps[0].packageName)
    }

    @Test
    fun searchChanged_emptyQueryShowsAll() = runTest {
        whenever(blockedAppRepository.getBlockedPackageNames()).thenReturn(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AppSelectionEvent.SearchChanged("insta"))
        viewModel.onEvent(AppSelectionEvent.SearchChanged(""))

        val state = viewModel.uiState.value as AppSelectionUiState.Success
        assertEquals(4, state.apps.size)
    }

    @Test
    fun save_persistsToRepositoryAndPrefs() = runTest {
        whenever(blockedAppRepository.getBlockedPackageNames()).thenReturn(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AppSelectionEvent.ToggleApp("com.instagram.android", true))
        viewModel.onEvent(AppSelectionEvent.Save)
        advanceUntilIdle()

        verify(blockedAppRepository).deleteAll()
        verify(blockedAppRepository).upsertAll(any())
        verify(preferences).syncBlockedApps(any())
    }

    @Test
    fun blockedCount_reflectsAllAppsNotFiltered() = runTest {
        whenever(blockedAppRepository.getBlockedPackageNames()).thenReturn(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AppSelectionEvent.ToggleApp("com.instagram.android", true))
        viewModel.onEvent(AppSelectionEvent.ToggleApp("com.twitter.android", true))
        viewModel.onEvent(AppSelectionEvent.SearchChanged("insta"))

        // Even though search shows only 1 app, blockedCount reflects all blocked
        val state = viewModel.uiState.value as AppSelectionUiState.Success
        assertEquals(1, state.apps.size)
        assertEquals(2, state.blockedCount)
    }
}
