package com.gratelock.app.ui.screen.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [OnboardingUiState] and [OnboardingEvent].
 *
 * Full ViewModel integration tests with Android context require instrumented tests.
 * These tests verify the state types, defaults, and derived properties.
 */
class OnboardingViewModelTest {

    // --- OnboardingUiState ---

    @Test
    fun onboardingUiState_defaultValues() {
        val state = OnboardingUiState()
        assertEquals(0, state.currentPage)
        assertFalse(state.isAccessibilityEnabled)
        assertFalse(state.isOverlayEnabled)
        assertFalse(state.isComplete)
    }

    @Test
    fun onboardingUiState_allPermissionsGranted_bothTrue() {
        val state = OnboardingUiState(
            isAccessibilityEnabled = true,
            isOverlayEnabled = true,
        )
        assertTrue(state.allPermissionsGranted)
    }

    @Test
    fun onboardingUiState_allPermissionsGranted_accessibilityOnly() {
        val state = OnboardingUiState(
            isAccessibilityEnabled = true,
            isOverlayEnabled = false,
        )
        assertFalse(state.allPermissionsGranted)
    }

    @Test
    fun onboardingUiState_allPermissionsGranted_overlayOnly() {
        val state = OnboardingUiState(
            isAccessibilityEnabled = false,
            isOverlayEnabled = true,
        )
        assertFalse(state.allPermissionsGranted)
    }

    @Test
    fun onboardingUiState_allPermissionsGranted_neitherTrue() {
        val state = OnboardingUiState(
            isAccessibilityEnabled = false,
            isOverlayEnabled = false,
        )
        assertFalse(state.allPermissionsGranted)
    }

    @Test
    fun onboardingUiState_pageConstants_areCorrect() {
        assertEquals(0, OnboardingUiState.PAGE_WELCOME)
        assertEquals(1, OnboardingUiState.PAGE_HOW_IT_WORKS)
        assertEquals(2, OnboardingUiState.PAGE_PERMISSIONS)
        assertEquals(3, OnboardingUiState.PAGE_TOTAL)
    }

    @Test
    fun onboardingUiState_customPage() {
        val state = OnboardingUiState(currentPage = 2)
        assertEquals(2, state.currentPage)
    }

    @Test
    fun onboardingUiState_isComplete_canBeTrue() {
        val state = OnboardingUiState(isComplete = true)
        assertTrue(state.isComplete)
    }

    // --- OnboardingEvent ---

    @Test
    fun onboardingEvent_nextPage_isCorrectType() {
        val event: OnboardingEvent = OnboardingEvent.NextPage
        assertTrue(event is OnboardingEvent.NextPage)
    }

    @Test
    fun onboardingEvent_previousPage_isCorrectType() {
        val event: OnboardingEvent = OnboardingEvent.PreviousPage
        assertTrue(event is OnboardingEvent.PreviousPage)
    }

    @Test
    fun onboardingEvent_openAccessibilitySettings_isCorrectType() {
        val event: OnboardingEvent = OnboardingEvent.OpenAccessibilitySettings
        assertTrue(event is OnboardingEvent.OpenAccessibilitySettings)
    }

    @Test
    fun onboardingEvent_openOverlaySettings_isCorrectType() {
        val event: OnboardingEvent = OnboardingEvent.OpenOverlaySettings
        assertTrue(event is OnboardingEvent.OpenOverlaySettings)
    }

    @Test
    fun onboardingEvent_refreshPermissions_isCorrectType() {
        val event: OnboardingEvent = OnboardingEvent.RefreshPermissions
        assertTrue(event is OnboardingEvent.RefreshPermissions)
    }

    @Test
    fun onboardingEvent_completeOnboarding_isCorrectType() {
        val event: OnboardingEvent = OnboardingEvent.CompleteOnboarding
        assertTrue(event is OnboardingEvent.CompleteOnboarding)
    }
}
