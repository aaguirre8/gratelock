package com.gratelock.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gratelock.app.ui.theme.GrateLockTheme
import com.gratelock.app.ui.theme.SageGreen
import com.gratelock.app.ui.theme.SageGreenLight
import com.gratelock.app.ui.theme.SoftBeige
import com.gratelock.app.ui.theme.WarmBeige

/**
 * Canvas-drawn happiness jar with animated fill level.
 *
 * The jar fills from the bottom as the user accumulates journal entries.
 * 100 entries = full jar. The fill animates smoothly when the level changes.
 *
 * @param fillPercent Fill level from 0f (empty) to 1f (full).
 * @param totalEntries Total number of entries displayed below the jar.
 * @param modifier Modifier for layout customization.
 */
@Composable
fun HappinessJar(
    fillPercent: Float,
    totalEntries: Int,
    modifier: Modifier = Modifier,
) {
    val animatedFill by animateFloatAsState(
        targetValue = fillPercent.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "jarFill",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp),
        ) {
            Canvas(modifier = Modifier.size(160.dp)) {
                drawJar(animatedFill)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (totalEntries == 1) "1 entry" else "$totalEntries entries",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Draws the jar shape with fill.
 *
 * @param fillPercent Animated fill level from 0f to 1f.
 */
private fun DrawScope.drawJar(fillPercent: Float) {
    val width = size.width
    val height = size.height

    // Jar dimensions
    val lidTop = height * 0.02f
    val lidBottom = height * 0.12f
    val lidInset = width * 0.25f
    val neckBottom = height * 0.18f
    val bodyTop = height * 0.22f
    val bodyBottom = height * 0.92f
    val bottomInset = width * 0.15f
    val bodyInset = width * 0.08f

    // Draw jar outline (body)
    val jarPath = Path().apply {
        // Left side from neck down
        moveTo(lidInset + width * 0.05f, neckBottom)
        // Curve out to body
        cubicTo(
            bodyInset, bodyTop,
            bodyInset, bodyTop + (bodyBottom - bodyTop) * 0.1f,
            bodyInset, bodyTop + (bodyBottom - bodyTop) * 0.2f,
        )
        lineTo(bodyInset, bodyBottom - height * 0.06f)
        // Bottom left curve
        cubicTo(
            bodyInset, bodyBottom,
            bottomInset, bodyBottom + height * 0.02f,
            bottomInset + width * 0.05f, bodyBottom + height * 0.02f,
        )
        // Bottom
        lineTo(width - bottomInset - width * 0.05f, bodyBottom + height * 0.02f)
        // Bottom right curve
        cubicTo(
            width - bottomInset, bodyBottom + height * 0.02f,
            width - bodyInset, bodyBottom,
            width - bodyInset, bodyBottom - height * 0.06f,
        )
        // Right side up
        lineTo(width - bodyInset, bodyTop + (bodyBottom - bodyTop) * 0.2f)
        cubicTo(
            width - bodyInset, bodyTop + (bodyBottom - bodyTop) * 0.1f,
            width - bodyInset, bodyTop,
            width - lidInset - width * 0.05f, neckBottom,
        )
        close()
    }

    // Fill the jar
    if (fillPercent > 0f) {
        val fillableHeight = bodyBottom - bodyTop
        val fillTop = bodyBottom - (fillableHeight * fillPercent)

        clipPath(jarPath) {
            drawRect(
                color = SageGreenLight,
                topLeft = Offset(0f, fillTop),
                size = Size(width, bodyBottom - fillTop + height * 0.05f),
            )
        }
    }

    // Draw jar body outline
    drawPath(
        path = jarPath,
        color = WarmBeige,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()),
    )

    // Draw lid
    drawRoundRect(
        color = SoftBeige,
        topLeft = Offset(lidInset, lidTop),
        size = Size(width - lidInset * 2, lidBottom - lidTop),
        cornerRadius = CornerRadius(6.dp.toPx()),
    )
    drawRoundRect(
        color = WarmBeige,
        topLeft = Offset(lidInset, lidTop),
        size = Size(width - lidInset * 2, lidBottom - lidTop),
        cornerRadius = CornerRadius(6.dp.toPx()),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()),
    )
}

@Preview(showBackground = true)
@Composable
private fun HappinessJarEmptyPreview() {
    GrateLockTheme {
        HappinessJar(fillPercent = 0f, totalEntries = 0)
    }
}

@Preview(showBackground = true)
@Composable
private fun HappinessJarHalfPreview() {
    GrateLockTheme {
        HappinessJar(fillPercent = 0.5f, totalEntries = 50)
    }
}

@Preview(showBackground = true)
@Composable
private fun HappinessJarFullPreview() {
    GrateLockTheme {
        HappinessJar(fillPercent = 1.0f, totalEntries = 100)
    }
}
