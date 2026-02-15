package com.gratelock.app.ui.component

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gratelock.app.ui.theme.GrateLockTheme

/**
 * Today's journal/unlock status indicator.
 *
 * Shows whether the user has journaled today and whether apps are unlocked.
 *
 * @param isUnlockedToday Whether apps are unlocked for today.
 * @param modifier Modifier for layout customization.
 */
@Composable
fun TodayStatus(
    isUnlockedToday: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isUnlockedToday) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val textColor = if (isUnlockedToday) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = if (isUnlockedToday) "\u2705" else "\uD83D\uDCDD",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = if (isUnlockedToday) "Unlocked for today" else "Journal to unlock your apps",
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TodayStatusLockedPreview() {
    GrateLockTheme {
        TodayStatus(isUnlockedToday = false)
    }
}

@Preview(showBackground = true)
@Composable
private fun TodayStatusUnlockedPreview() {
    GrateLockTheme {
        TodayStatus(isUnlockedToday = true)
    }
}
