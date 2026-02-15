package com.gratelock.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gratelock.app.ui.theme.CardSurface
import com.gratelock.app.ui.theme.GrateLockTheme

/**
 * Card displaying a typed gratitude entry's three items.
 *
 * Shows the date at the top and up to three gratitude items below.
 * Used in the Jar gallery and entry detail views.
 *
 * @param date The entry date formatted for display (e.g. "Feb 15, 2026").
 * @param gratitude1 First gratitude item.
 * @param gratitude2 Second gratitude item.
 * @param gratitude3 Third gratitude item.
 * @param onClick Callback when the card is tapped.
 * @param modifier Modifier for layout customization.
 */
@Composable
fun GratitudeCard(
    date: String,
    gratitude1: String,
    gratitude2: String?,
    gratitude3: String?,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardSurface)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
        )

        Spacer(modifier = Modifier.height(8.dp))

        GratitudeItem(number = 1, text = gratitude1)

        if (gratitude2 != null) {
            Spacer(modifier = Modifier.height(4.dp))
            GratitudeItem(number = 2, text = gratitude2)
        }

        if (gratitude3 != null) {
            Spacer(modifier = Modifier.height(4.dp))
            GratitudeItem(number = 3, text = gratitude3)
        }
    }
}

/**
 * Single numbered gratitude item within a card.
 *
 * @param number The item number (1-3).
 * @param text The gratitude text.
 */
@Composable
private fun GratitudeItem(
    number: Int,
    text: String,
) {
    Text(
        text = "$number. $text",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Preview(showBackground = true)
@Composable
private fun GratitudeCardPreview() {
    GrateLockTheme {
        GratitudeCard(
            date = "Feb 15, 2026",
            gratitude1 = "Morning coffee with a friend",
            gratitude2 = "Sunny weather on my walk home",
            gratitude3 = "A great conversation with my mom",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GratitudeCardSingleItemPreview() {
    GrateLockTheme {
        GratitudeCard(
            date = "Feb 14, 2026",
            gratitude1 = "Had a really productive day at work today",
            gratitude2 = null,
            gratitude3 = null,
        )
    }
}
