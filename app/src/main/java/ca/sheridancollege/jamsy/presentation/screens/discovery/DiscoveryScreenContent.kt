package ca.sheridancollege.jamsy.presentation.screens.discovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White
import ca.sheridancollege.jamsy.presentation.viewmodels.DiscoveryViewModel
import ca.sheridancollege.jamsy.util.Resource

/**
 * Displays the appropriate content based on track loading state
 */
@Composable
fun DiscoveryContent(
    tracksState: Resource<List<Track>>,
    currentTrackIndex: Int,
    likedTracks: List<Track>,
    dragOffset: Float,
    isProcessingLike: Boolean,
    isProcessingDislike: Boolean,
    onDragOffsetChange: (Float) -> Unit,
    onProcessingLikeChange: (Boolean) -> Unit,
    onProcessingDislikeChange: (Boolean) -> Unit,
    onDragEnd: (Float) -> Unit,
    onNavigateToGeneratedPlaylist: () -> Unit,
    onBack: () -> Unit,
    viewModel: DiscoveryViewModel,
    authToken: String
) {
    when (tracksState) {
        is Resource.Loading -> DiscoveryLoadingContent()
        is Resource.Error -> DiscoveryErrorContent(onBack)
        is Resource.Success -> {
            if (tracksState.data.isEmpty()) {
                DiscoveryEmptyContent(onNavigateToGeneratedPlaylist, likedTracks.isNotEmpty())
            } else {
                val currentTrack = viewModel.getCurrentTrack()
                if (currentTrack != null) {
                    DiscoverySuccessContent(
                        currentTrack = currentTrack,
                        tracks = tracksState.data,
                        currentTrackIndex = currentTrackIndex,
                        dragOffset = dragOffset,
                        isProcessingLike = isProcessingLike,
                        isProcessingDislike = isProcessingDislike,
                        onDragOffsetChange = onDragOffsetChange,
                        onProcessingLikeChange = onProcessingLikeChange,
                        onProcessingDislikeChange = onProcessingDislikeChange,
                        onDragEnd = onDragEnd,
                        onNavigateToGeneratedPlaylist = onNavigateToGeneratedPlaylist,
                        viewModel = viewModel,
                        authToken = authToken
                    )
                }
            }
        }
    }
}

/**
 * Displays loading state with spinner and message
 */
@Composable
fun DiscoveryLoadingContent() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = SpotifyGreen)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading discovery tracks...",
                style = MaterialTheme.typography.bodyMedium,
                color = LightGray
            )
        }
    }
}

/**
 * Displays error state when tracks fail to load
 */
@Composable
fun DiscoveryErrorContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎵 Ready to Discover Music?",
            style = MaterialTheme.typography.headlineSmall,
            color = SpotifyGreen,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "To discover new tracks, please select your favorite artists first. This helps us recommend music you'll love!",
            style = MaterialTheme.typography.bodyMedium,
            color = LightGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen)
        ) {
            Text("Select Artists", color = White)
        }
    }
}

/**
 * Displays empty state when all tracks have been reviewed
 */
@Composable
fun DiscoveryEmptyContent(
    onNavigateToGeneratedPlaylist: () -> Unit,
    hasLikedTracks: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉 Discovery Complete!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = White
        )
        Text(
            text = "You've discovered all available tracks.",
            style = MaterialTheme.typography.bodyMedium,
            color = LightGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateToGeneratedPlaylist,
            enabled = hasLikedTracks,
            colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen)
        ) {
            Text("View Your Liked Tracks", color = White)
        }
    }
}

/**
 * Displays track card with action buttons and playback controls
 */
@Composable
fun DiscoverySuccessContent(
    currentTrack: Track,
    tracks: List<Track>,
    currentTrackIndex: Int,
    dragOffset: Float,
    isProcessingLike: Boolean,
    isProcessingDislike: Boolean,
    onDragOffsetChange: (Float) -> Unit,
    onProcessingLikeChange: (Boolean) -> Unit,
    onProcessingDislikeChange: (Boolean) -> Unit,
    onDragEnd: (Float) -> Unit,
    onNavigateToGeneratedPlaylist: () -> Unit,
    viewModel: DiscoveryViewModel,
    authToken: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DiscoveryTrackCard(
            track = currentTrack,
            dragOffset = dragOffset,
            onDragEnd = onDragEnd,
            onDragUpdate = onDragOffsetChange
        )

        // Action buttons at bottom
        DiscoveryActionButtonsRow(
            isProcessingLike = isProcessingLike,
            isProcessingDislike = isProcessingDislike,
            currentTrack = currentTrack,
            onProcessingLikeChange = onProcessingLikeChange,
            onProcessingDislikeChange = onProcessingDislikeChange,
            viewModel = viewModel,
            authToken = authToken,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Progress indicator
        if (tracks.size > 1) {
            LinearProgressIndicator(
                progress = (currentTrackIndex + 1).toFloat() / tracks.size,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = SpotifyGreen
            )
        }

        // View Generated Playlist button
        if (currentTrackIndex >= tracks.size - 1) {
            Button(
                onClick = onNavigateToGeneratedPlaylist,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen)
            ) {
                Text(
                    "View Generated Playlist",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }
    }
}
