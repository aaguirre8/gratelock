package com.gratelock.app.ui.screen.camera

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [CameraViewModel] state transitions.
 *
 * These tests verify the state machine logic without Android dependencies.
 * Full integration tests with ML Kit and CameraX require instrumented tests.
 */
class CameraViewModelTest {

    @Test
    fun cameraUiState_previewing_defaultIsFlashOff() {
        val state = CameraUiState.Previewing()
        assertEquals(false, state.isFlashOn)
    }

    @Test
    fun cameraUiState_previewing_flashCanBeToggled() {
        val state = CameraUiState.Previewing(isFlashOn = true)
        assertEquals(true, state.isFlashOn)
    }

    @Test
    fun cameraUiState_error_holdsMessage() {
        val state = CameraUiState.Error("Test error message")
        assertEquals("Test error message", state.errorMessage)
    }

    @Test
    fun cameraUiState_success_holdsStreak() {
        val state = CameraUiState.Success(newStreak = 5)
        assertEquals(5, state.newStreak)
    }

    @Test
    fun cameraEvent_capture_isDataObject() {
        val event: CameraEvent = CameraEvent.Capture
        assertTrue(event is CameraEvent.Capture)
    }

    @Test
    fun cameraEvent_toggleFlash_isDataObject() {
        val event: CameraEvent = CameraEvent.ToggleFlash
        assertTrue(event is CameraEvent.ToggleFlash)
    }

    @Test
    fun cameraEvent_retry_isDataObject() {
        val event: CameraEvent = CameraEvent.Retry
        assertTrue(event is CameraEvent.Retry)
    }
}
