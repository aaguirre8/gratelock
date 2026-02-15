package com.gratelock.app.ui.screen.overlay

/**
 * Events that can be triggered from the blocking overlay screen UI.
 */
sealed interface BlockingOverlayEvent {

    /** User chose to type 3 things they're grateful for. */
    data object TypeEntry : BlockingOverlayEvent

    /** User chose to photograph their journal. */
    data object PhotoEntry : BlockingOverlayEvent

    /** User tapped emergency unlock. */
    data object EmergencyUnlock : BlockingOverlayEvent
}
