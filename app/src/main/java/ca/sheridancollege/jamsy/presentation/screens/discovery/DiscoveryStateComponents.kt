package ca.sheridancollege.jamsy.presentation.screens.discovery

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ca.sheridancollege.jamsy.presentation.components.GlassCard
import ca.sheridancollege.jamsy.presentation.components.PremiumGradientButton
import ca.sheridancollege.jamsy.presentation.theme.Gray
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White

/**
 * Premium loading state with glassmorphic design and animations.
 * Single Responsibility: Display premium loading UI
 */
@Composable
fun DiscoveryPremiumLoadingState(animationDelay: Int = 300) {
    var contentAlpha by remember { mutableFloatStateOf(0f) }

    val animatedAlpha by animateFloatAsState(
        targetValue = contentAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = animationDelay),
        label = "loading_alpha"
    )

    LaunchedEffect(Unit) {
        contentAlpha = 1f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Loading indicator with glass effect
        GlassCard(
            modifier = Modifier
                .alpha(animatedAlpha)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = SpotifyGreen,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Loading discovery tracks...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = White,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Finding hidden gems for you",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Premium empty state with glassmorphic design and animations.
 * Single Responsibility: Display premium empty state UI
 */
@Composable
fun DiscoveryPremiumEmptyState(
    onNavigateToGeneratedPlaylist: () -> Unit,
    hasLikedTracks: Boolean,
    animationDelay: Int = 300
) {
    var contentAlpha by remember { mutableFloatStateOf(0f) }

    val animatedAlpha by animateFloatAsState(
        targetValue = contentAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = animationDelay),
        label = "empty_alpha"
    )

    LaunchedEffect(Unit) {
        contentAlpha = 1f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Content card with glass effect
        GlassCard(
            modifier = Modifier
                .alpha(animatedAlpha)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🎉",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Discovery Complete!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You've discovered all available tracks.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "You've liked ${if (hasLikedTracks) "some amazing" else "0"} tracks!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action button
        PremiumGradientButton(
            text = if (hasLikedTracks) "View Your Liked Tracks" else "Select Artists",
            onClick = onNavigateToGeneratedPlaylist,
            modifier = Modifier
                .alpha(animatedAlpha)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

/**
 * Premium error state with glassmorphic design and animations.
 * Single Responsibility: Display premium error state UI
 */
@Composable
fun DiscoveryPremiumErrorState(
    onBack: () -> Unit,
    animationDelay: Int = 300
) {
    var contentAlpha by remember { mutableFloatStateOf(0f) }

    val animatedAlpha by animateFloatAsState(
        targetValue = contentAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = animationDelay),
        label = "error_alpha"
    )

    LaunchedEffect(Unit) {
        contentAlpha = 1f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error card with glass effect
        GlassCard(
            modifier = Modifier
                .alpha(animatedAlpha)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🎵",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Ready to Discover Music?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = SpotifyGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "To discover new tracks, please select your favorite artists first.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "This helps us recommend music you'll love!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action button
        PremiumGradientButton(
            text = "Select Artists",
            onClick = onBack,
            modifier = Modifier
                .alpha(animatedAlpha)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

/**
 * Premium stats card showing discovery progress.
 * Single Responsibility: Display discovery statistics
 */
@Composable
fun DiscoveryStatsCard(
    currentIndex: Int,
    totalTracks: Int,
    likedCount: Int,
    animationDelay: Int = 500
) {
    var statsAlpha by remember { mutableFloatStateOf(0f) }

    val animatedAlpha by animateFloatAsState(
        targetValue = statsAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = animationDelay),
        label = "stats_alpha"
    )

    LaunchedEffect(Unit) {
        statsAlpha = 1f
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(animatedAlpha)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                label = "Track",
                value = "${currentIndex + 1}/$totalTracks"
            )
            
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(
                        color = SpotifyGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )

            StatItem(
                label = "Liked",
                value = "$likedCount",
                isHighlight = true
            )
        }
    }
}

/**
 * Single stat item component.
 * Single Responsibility: Display a single stat value
 */
@Composable
private fun StatItem(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isHighlight) SpotifyGreen else LightGray,
            fontSize = 10.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = if (isHighlight) SpotifyGreen else White,
            fontSize = 14.sp
        )
    }
}

