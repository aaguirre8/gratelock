package com.gratelock.app.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

/**
 * Utility for checking and requesting the permissions required by GrateLock.
 */
object PermissionUtils {

    /**
     * Checks if the GrateLock accessibility service is currently enabled.
     *
     * @param context Application or activity context.
     * @return True if the accessibility service is running.
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_GENERIC,
        )
        val ownPackage = context.packageName
        return enabledServices.any {
            it.resolveInfo.serviceInfo.packageName == ownPackage
        }
    }

    /**
     * Checks if the app has overlay (draw over other apps) permission.
     *
     * @param context Application or activity context.
     * @return True if the overlay permission is granted.
     */
    fun canDrawOverlays(context: Context): Boolean =
        Settings.canDrawOverlays(context)

    /**
     * Opens the system accessibility settings screen.
     *
     * @param context Activity context.
     */
    fun openAccessibilitySettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }

    /**
     * Opens the system overlay permission settings for this app.
     *
     * @param context Activity context.
     */
    fun openOverlaySettings(context: Context) {
        context.startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                android.net.Uri.parse("package:${context.packageName}"),
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }
}
