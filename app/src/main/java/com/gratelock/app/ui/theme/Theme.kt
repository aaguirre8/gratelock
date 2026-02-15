package com.gratelock.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GrateLockColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = Cream,
    primaryContainer = SageGreenLight,
    onPrimaryContainer = DarkWarm,
    secondary = DustyRose,
    onSecondary = Cream,
    secondaryContainer = DustyRoseLight,
    onSecondaryContainer = DarkWarm,
    tertiary = WarningAmber,
    onTertiary = Cream,
    background = Cream,
    onBackground = DarkWarm,
    surface = SurfaceWarm,
    onSurface = DarkWarm,
    surfaceVariant = SoftBeige,
    onSurfaceVariant = MediumWarm,
    outline = LightWarm,
    error = ErrorRose,
    onError = Cream,
)

/**
 * GrateLock app theme wrapping Material 3 with warm, minimal aesthetics.
 *
 * Uses cream/beige neutrals with sage green as primary accent
 * and dusty rose as secondary. Nunito font for headers.
 */
@Composable
fun GrateLockTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GrateLockColorScheme,
        typography = GrateLockTypography,
        content = content,
    )
}
