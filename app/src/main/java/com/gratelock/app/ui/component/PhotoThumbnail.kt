package com.gratelock.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gratelock.app.ui.theme.GrateLockTheme
import com.gratelock.app.ui.theme.SoftBeige
import java.io.File

/**
 * Rounded image thumbnail for photo journal entries.
 *
 * Uses Coil's [AsyncImage] to load photos from internal storage.
 * Shows a placeholder with the date if the photo can't be loaded.
 *
 * @param photoPath Local file path to the photo.
 * @param date Entry date for the overlay label.
 * @param onClick Callback when the thumbnail is tapped.
 * @param modifier Modifier for layout customization.
 */
@Composable
fun PhotoThumbnail(
    photoPath: String,
    date: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = File(photoPath),
            contentDescription = "Journal photo from $date",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Date label overlay at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(
                    color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(topEnd = 8.dp),
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

/**
 * Placeholder thumbnail shown when no photo path is available.
 *
 * @param date Entry date for display.
 * @param onClick Callback when tapped.
 * @param modifier Modifier for layout customization.
 */
@Composable
fun PhotoThumbnailPlaceholder(
    date: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(SoftBeige)
            .clickable(onClick = onClick),
    ) {
        Text(
            text = "\uD83D\uDCF7\n$date",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoThumbnailPlaceholderPreview() {
    GrateLockTheme {
        PhotoThumbnailPlaceholder(date = "Feb 15")
    }
}
