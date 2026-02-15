package com.gratelock.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.ui.screen.overlay.BlockingOverlayActivity
import com.gratelock.app.util.Constants

/**
 * Accessibility service that monitors window changes and intercepts
 * blocked app launches with the gratitude overlay.
 *
 * Reads from [GrateLockPreferences] for speed — SharedPreferences is
 * a fast-access cache synced from Room whenever data changes.
 *
 * Uses a 500ms debounce to prevent stacking from rapid app switching.
 */
class BlockingAccessibilityService : AccessibilityService() {

    private var lastOverlayTime = 0L
    private var prefs: GrateLockPreferences? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = GrateLockPreferences(applicationContext)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        val preferences = prefs ?: return

        // Skip if blocking is disabled or already unlocked
        if (!preferences.blockingEnabled) return
        if (preferences.isUnlockedToday) return

        // Skip system UI and our own package
        if (packageName == this.packageName) return
        if (packageName == "com.android.systemui") return
        if (packageName == "com.android.launcher") return
        if (packageName.startsWith("com.android.launcher")) return
        if (packageName.startsWith("com.sec.android.app.launcher")) return // Samsung
        if (packageName.startsWith("com.google.android.apps.nexuslauncher")) return

        // Check if the app is in the blocked set
        if (packageName !in preferences.blockedPackages) return

        // Debounce: don't re-launch overlay within 500ms
        val now = System.currentTimeMillis()
        if (now - lastOverlayTime < Constants.OVERLAY_DEBOUNCE_MS) return
        lastOverlayTime = now

        // Launch the blocking overlay
        val intent = Intent(this, BlockingOverlayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(BlockingOverlayActivity.EXTRA_BLOCKED_PACKAGE, packageName)
        }
        startActivity(intent)
    }

    override fun onInterrupt() {
        // Required override — nothing to clean up
    }
}
