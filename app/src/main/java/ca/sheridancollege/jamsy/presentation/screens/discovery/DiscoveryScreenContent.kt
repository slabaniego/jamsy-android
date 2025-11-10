package ca.sheridancollege.jamsy.presentation.screens.discovery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.presentation.components.PremiumButton
import ca.sheridancollege.jamsy.presentation.viewmodels.DiscoveryViewModel
import ca.sheridancollege.jamsy.util.Resource

/**
 * Displays the appropriate content based on track loading state.
 * Orchestrates premium UI components based on resource state.
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
        is Resource.Loading -> DiscoveryPremiumLoadingState()
        is Resource.Error -> DiscoveryPremiumErrorState(onBack)
        is Resource.Success -> {
            if (tracksState.data.isEmpty()) {
                DiscoveryPremiumEmptyState(
                    onNavigateToGeneratedPlaylist = onNavigateToGeneratedPlaylist,
                    hasLikedTracks = likedTracks.isNotEmpty()
                )
            } else {
                val currentTrack = viewModel.getCurrentTrack()
                if (currentTrack != null) {
                    DiscoverySuccessContent(
                        currentTrack = currentTrack,
                        tracks = tracksState.data,
                        currentTrackIndex = currentTrackIndex,
                        likedTracksCount = likedTracks.size,
                        dragOffset = dragOffset,
                        isProcessingLike = isProcessingLike,
                        isProcessingDislike = isProcessingDislike,
                        onDragOffsetChange = onDragOffsetChange,
                        onProcessingLikeChange = onProcessingLikeChange,
                        onProcessingDislikeChange = onProcessingDislikeChange,
                        onDragEnd = onDragEnd,
                        onNavigateToGeneratedPlaylist = onNavigateToGeneratedPlaylist,
                        //viewModel = viewModel,
                        //authToken = authToken
                    )
                }
            }
        }
    }
}

/**
 * Displays track card with action buttons and playback controls.
 * Premium version with glassmorphic components and animations.
 */
@Composable
fun DiscoverySuccessContent(
    currentTrack: Track,
    tracks: List<Track>,
    currentTrackIndex: Int,
    likedTracksCount: Int,
    dragOffset: Float,
    isProcessingLike: Boolean,
    isProcessingDislike: Boolean,
    onDragOffsetChange: (Float) -> Unit,
    onProcessingLikeChange: (Boolean) -> Unit,
    onProcessingDislikeChange: (Boolean) -> Unit,
    onDragEnd: (Float) -> Unit,
    onNavigateToGeneratedPlaylist: () -> Unit,
    //viewModel: DiscoveryViewModel,
    //authToken: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main track card in center
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            DiscoveryTrackCard(
                track = currentTrack,
                dragOffset = dragOffset,
                onDragEnd = onDragEnd,
                onDragUpdate = onDragOffsetChange
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats card
        DiscoveryStatsCard(
            currentIndex = currentTrackIndex,
            totalTracks = tracks.size,
            likedCount = likedTracksCount,
            animationDelay = 500
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (currentTrackIndex >= tracks.size - 1) {
            PremiumButton(
                text = "View Generated Playlist",
                onClick = onNavigateToGeneratedPlaylist,
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                fontSize = 14
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
