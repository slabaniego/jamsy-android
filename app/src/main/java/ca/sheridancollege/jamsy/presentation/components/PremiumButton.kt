/*
 * PremiumButton.kt
 * Reusable green gradient button used across premium UI screens.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen

@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: Int = 12
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        SpotifyGreen.copy(alpha = if (enabled) 0.2f else 0.1f),
                        SpotifyGreen.copy(alpha = if (enabled) 0.1f else 0.05f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        SpotifyGreen.copy(alpha = if (enabled) 0.2f else 0.1f),
                        SpotifyGreen.copy(alpha = if (enabled) 0.1f else 0.05f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(2.dp)
    ) {
        Text(
            text,
            color = if (enabled) SpotifyGreen else LightGray,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

