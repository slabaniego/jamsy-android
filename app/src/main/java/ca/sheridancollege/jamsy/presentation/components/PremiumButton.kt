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

/**
 * Reusable premium button with green gradient background.
 * Used across multiple screens for consistent styling.
 *
 * @param text Button text to display
 * @param onClick Callback when button is clicked
 * @param modifier Optional modifier for customization
 * @param enabled Whether button is enabled (affects color)
 * @param fontSize Font size for text
 */
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

