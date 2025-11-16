package ca.sheridancollege.jamsy.presentation.screens.generated_playlist

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import ca.sheridancollege.jamsy.presentation.components.PremiumButton
import ca.sheridancollege.jamsy.presentation.theme.Gray
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White

/**
 * Premium loading state for generated playlist.
 */
@Composable
fun GeneratedPlaylistLoadingState(animationDelay: Int = 300) {
    var contentAlpha by remember { mutableStateOf(0f) }

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
        GlassCard(
            modifier = Modifier
                .alpha(animatedAlpha)
                .fillMaxSize(0.7f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                    text = "Creating Your Playlist...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = White,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Please wait while we generate your personalized playlist",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Premium empty state for generated playlist.
 */
@Composable
fun GeneratedPlaylistEmptyState(
    onBack: () -> Unit,
    animationDelay: Int = 300
) {
    var contentAlpha by remember { mutableStateOf(0f) }

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
        GlassCard(
            modifier = Modifier
                .alpha(animatedAlpha)
                .fillMaxSize(0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                    text = "No Tracks Found",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You haven't liked any tracks yet. Go back to the discovery screen and start liking some music!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                PremiumButton(
                    text = "Go Back to Discovery",
                    onClick = onBack,
                    modifier = Modifier.fillMaxSize(0.8f),
                    enabled = true,
                    fontSize = 14
                )
            }
        }
    }
}

/**
 * Premium error state for generated playlist.
 */
@Composable
fun GeneratedPlaylistErrorState(
    errorMessage: String,
    onBack: () -> Unit,
    animationDelay: Int = 300
) {
    var contentAlpha by remember { mutableStateOf(0f) }

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
        GlassCard(
            modifier = Modifier
                .alpha(animatedAlpha)
                .fillMaxSize(0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error loading playlist",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                PremiumButton(
                    text = "Try Again",
                    onClick = onBack,
                    modifier = Modifier.fillMaxSize(0.8f),
                    enabled = true,
                    fontSize = 14
                )
            }
        }
    }
}

