package ca.sheridancollege.jamsy.presentation.screens.discovery

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ca.sheridancollege.jamsy.presentation.components.GlassCard
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen

/**
 * Premium progress indicator with glassmorphic design and animations.
 * Shows track progress with smooth animations.
 *
 * Single Responsibility: Display premium progress indicator
 */
@Composable
fun DiscoveryPremiumProgressIndicator(
    currentIndex: Int,
    totalTracks: Int,
    animationDelay: Int = 100
) {
    if (totalTracks <= 1) return

    var progressAlpha by remember { mutableFloatStateOf(0f) }

    val animatedAlpha by animateFloatAsState(
        targetValue = progressAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = animationDelay),
        label = "progress_alpha"
    )

    val progress = (currentIndex + 1).toFloat() / totalTracks

    LaunchedEffect(Unit) {
        progressAlpha = 1f
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(animatedAlpha)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Progress bar container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                SpotifyGreen.copy(alpha = 0.2f),
                                SpotifyGreen.copy(alpha = 0.1f)
                            )
                        )
                    )
            ) {
                // Animated progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    SpotifyGreen,
                                    SpotifyGreen.copy(alpha = 0.8f),
                                    SpotifyGreen
                                )
                            )
                        )
                )
            }

            // Progress text
            Text(
                text = "${currentIndex + 1} of $totalTracks",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = LightGray,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            )
        }
    }
}

