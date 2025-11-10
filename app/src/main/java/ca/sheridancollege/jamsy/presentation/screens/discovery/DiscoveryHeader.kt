package ca.sheridancollege.jamsy.presentation.screens.discovery

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ca.sheridancollege.jamsy.presentation.components.GlassCard
import ca.sheridancollege.jamsy.presentation.components.PremiumButton
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White

/**
 * Premium header for Discovery screen with glassmorphic design.
 * Displays title, track count, and navigation controls.
 *
 * Single Responsibility: Display premium header UI with animations
 */
@Composable
fun DiscoveryHeader(
    likedTracksCount: Int,
    onBack: () -> Unit,
    onViewLiked: () -> Unit,
    hasLikedTracks: Boolean,
    animationDelay: Int = 100
) {
    // Animation state
    var titleAlpha by remember { mutableFloatStateOf(0f) }
    
    val animatedAlpha by animateFloatAsState(
        targetValue = titleAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = animationDelay),
        label = "header_alpha"
    )
    
    LaunchedEffect(Unit) {
        titleAlpha = 1f
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(animatedAlpha)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                SpotifyGreen.copy(alpha = 0.2f),
                                SpotifyGreen.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SpotifyGreen
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title and counter column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Discover Music",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    "$likedTracksCount tracks liked",
                    fontSize = 12.sp,
                    color = LightGray
                )
            }

            // View Liked button
            PremiumButton(
                text = "View",
                onClick = onViewLiked,
                enabled = hasLikedTracks,
                fontSize = 12
            )
        }
    }
}

