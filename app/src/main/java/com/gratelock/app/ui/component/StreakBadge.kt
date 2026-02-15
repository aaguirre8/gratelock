package com.gratelock.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gratelock.app.ui.theme.GrateLockTheme

/**
 * Streak badge showing the current consecutive-day count.
 *
 * Displays a fire emoji alongside the streak number and a label.
 * Includes the longest streak below when it differs from current.
 *
 * @param currentStreak The current consecutive-day streak.
 * @param longestStreak The all-time longest streak.
 * @param modifier Modifier for layout customization.
 */
@Composable
fun StreakBadge(
    currentStreak: Int,
    longestStreak: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "\uD83D\uDD25",
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "$currentStreak",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Text(
            text = if (currentStreak == 1) "day streak" else "day streak",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (longestStreak > currentStreak) {
            Text(
                text = "Best: $longestStreak days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakBadgeZeroPreview() {
    GrateLockTheme {
        StreakBadge(currentStreak = 0, longestStreak = 0)
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakBadgeActivePreview() {
    GrateLockTheme {
        StreakBadge(currentStreak = 7, longestStreak = 30)
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakBadgeBestPreview() {
    GrateLockTheme {
        StreakBadge(currentStreak = 30, longestStreak = 30)
    }
}
