package com.gratelock.app.ui.screen.camera

import java.io.File

/**
 * Events that can be triggered from the Camera screen UI.
 */
sealed interface CameraEvent {

    /** User tapped the capture button. */
    data object Capture : CameraEvent

    /** User toggled flash on/off. */
    data object ToggleFlash : CameraEvent

    /**
     * Photo saved to file by CameraX.
     *
     * @param file The saved photo file.
     */
    data class PhotoSaved(val file: File) : CameraEvent

    /** User wants to retry after a validation error. */
    data object Retry : CameraEvent
}
