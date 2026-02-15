package com.gratelock.app.ui.navigation

/**
 * Sealed class defining all navigation routes in the app.
 *
 * Routes are defined as objects (no arguments) or data classes (with arguments).
 * Never use hardcoded strings for navigation — always reference these routes.
 *
 * @param route The navigation route string identifier.
 */
sealed class Screen(val route: String) {

    // Bottom nav destinations
    data object Home : Screen("home")
    data object Jar : Screen("jar")
    data object Wrap : Screen("wrap")
    data object Settings : Screen("settings")

    // Onboarding
    data object Onboarding : Screen("onboarding")

    // App selection (used in onboarding and settings)
    data object AppSelection : Screen("app_selection")

    // Entry flow (hosted inside BlockingOverlayActivity)
    data object GratitudeForm : Screen("gratitude_form")
    data object Camera : Screen("camera")
    data object Validation : Screen("validation")
    data object Success : Screen("success")

    // Detail views
    data object EntryDetail : Screen("entry_detail/{entryId}") {
        /** @param entryId The journal entry ID to display. */
        fun createRoute(entryId: Long): String = "entry_detail/$entryId"
    }
}
