package com.gratelock.app.ui.screen.onboarding

/**
 * Events that can be triggered from the Onboarding screen UI.
 */
sealed interface OnboardingEvent {

    /** Navigate to the next page. */
    data object NextPage : OnboardingEvent

    /** Navigate to the previous page. */
    data object PreviousPage : OnboardingEvent

    /** Open the accessibility service settings. */
    data object OpenAccessibilitySettings : OnboardingEvent

    /** Open the overlay permission settings. */
    data object OpenOverlaySettings : OnboardingEvent

    /** Refresh permission states (e.g., when returning from settings). */
    data object RefreshPermissions : OnboardingEvent

    /** Complete onboarding and navigate to Home. */
    data object CompleteOnboarding : OnboardingEvent
}
