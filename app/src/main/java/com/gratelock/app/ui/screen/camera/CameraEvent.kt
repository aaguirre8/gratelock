package com.gratelock.app.ui.screen.camera

import android.graphics.Bitmap

/**
 * Events that can be triggered from the Camera screen UI.
 */
sealed interface CameraEvent {

    /** User tapped the capture button. */
    data object Capture : CameraEvent

    /** User toggled flash on/off. */
    data object ToggleFlash : CameraEvent

    /**
     * Photo bitmap is ready from CameraX capture.
     *
     * @param bitmap The captured photo bitmap.
     */
    data class PhotoCaptured(val bitmap: Bitmap) : CameraEvent

    /** User wants to retry after a validation error. */
    data object Retry : CameraEvent
}
