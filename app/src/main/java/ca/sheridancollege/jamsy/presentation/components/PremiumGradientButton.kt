package ca.sheridancollege.jamsy.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White

/**
 * Premium gradient button with smooth animations.
 */
@Composable
fun PremiumGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        animationSpec = tween(durationMillis = 200),
        label = "button_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        SpotifyGreen,
                        SpotifyGreen.copy(alpha = 0.8f),
                        SpotifyGreen
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .clickable(enabled = enabled && !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = White,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            Text(
                text = text,
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

