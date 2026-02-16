package com.gratelock.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gratelock.app.ui.theme.CardSurface
import com.gratelock.app.ui.theme.GrateLockTheme

/**
 * Reusable permission status card for the onboarding flow.
 *
 * Shows the permission name, description, current status (granted/not),
 * and a button to open the relevant settings screen.
 *
 * @param title The permission name (e.g. "Accessibility Service").
 * @param description Why this permission is needed.
 * @param isGranted Whether the permission is currently granted.
 * @param onGrant Callback when the user taps the grant/open settings button.
 * @param modifier Modifier for layout customization.
 */
@Composable
fun PermissionCard(
    title: String,
    description: String,
    isGranted: Boolean,
    onGrant: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = CardSurface,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = if (isGranted) "\u2705" else "\u26A0\uFE0F",
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (!isGranted) {
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onGrant,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Enable",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PermissionCardGrantedPreview() {
    GrateLockTheme {
        PermissionCard(
            title = "Accessibility Service",
            description = "Detects when you open a blocked app",
            isGranted = true,
            onGrant = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PermissionCardNotGrantedPreview() {
    GrateLockTheme {
        PermissionCard(
            title = "Overlay Permission",
            description = "Shows the journaling screen over blocked apps",
            isGranted = false,
            onGrant = {},
        )
    }
}
