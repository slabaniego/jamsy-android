package ca.sheridancollege.jamsy.presentation.screens.discovery

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White
import ca.sheridancollege.jamsy.presentation.viewmodels.DiscoveryViewModel

private const val TAG = "DiscoveryActionButtons"
private const val FAB_SIZE = 64
private const val BUTTON_SPACING = 64
private const val BUTTON_PADDING = 32

/**
 * Row containing like and dislike action buttons
 */
@Composable
fun DiscoveryActionButtonsRow(
    isProcessingLike: Boolean,
    isProcessingDislike: Boolean,
    currentTrack: Track,
    onProcessingLikeChange: (Boolean) -> Unit,
    onProcessingDislikeChange: (Boolean) -> Unit,
    viewModel: DiscoveryViewModel,
    authToken: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(BUTTON_PADDING.dp),
        horizontalArrangement = Arrangement.spacedBy(BUTTON_SPACING.dp)
    ) {
        DiscoveryDislikeButton(
            isProcessing = isProcessingDislike,
            currentTrack = currentTrack,
            onProcessingChange = onProcessingDislikeChange,
            viewModel = viewModel,
            authToken = authToken
        )

        DiscoveryLikeButton(
            isProcessing = isProcessingLike,
            currentTrack = currentTrack,
            onProcessingChange = onProcessingLikeChange,
            viewModel = viewModel,
            authToken = authToken
        )
    }
}

/**
 * Dislike action button with independent animation state
 */
@Composable
fun DiscoveryDislikeButton(
    isProcessing: Boolean,
    currentTrack: Track,
    onProcessingChange: (Boolean) -> Unit,
    viewModel: DiscoveryViewModel,
    authToken: String
) {
    FloatingActionButton(
        onClick = {
            Log.d(TAG, "Dislike clicked for: ${currentTrack.name}")
            onProcessingChange(true)
            viewModel.handleTrackAction(
                track = currentTrack,
                action = "dislike",
                authToken = authToken,
                onComplete = { onProcessingChange(false) }
            )
        },
        modifier = Modifier.size(FAB_SIZE.dp),
        containerColor = if (isProcessing) 
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.errorContainer,
        contentColor = if (isProcessing)
            MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.onErrorContainer
    ) {
        Icon(Icons.Default.ThumbDown, contentDescription = "Dislike")
    }
}

/**
 * Like action button with independent animation state
 */
@Composable
fun DiscoveryLikeButton(
    isProcessing: Boolean,
    currentTrack: Track,
    onProcessingChange: (Boolean) -> Unit,
    viewModel: DiscoveryViewModel,
    authToken: String
) {
    FloatingActionButton(
        onClick = {
            Log.d(TAG, "Like clicked for: ${currentTrack.name}")
            onProcessingChange(true)
            viewModel.handleTrackAction(
                track = currentTrack,
                action = "like",
                authToken = authToken,
                onComplete = { onProcessingChange(false) }
            )
        },
        modifier = Modifier.size(FAB_SIZE.dp),
        containerColor = if (isProcessing)
            SpotifyGreen.copy(alpha = 0.5f)
        else SpotifyGreen,
        contentColor = if (isProcessing)
            White.copy(alpha = 0.5f)
        else White
    ) {
        Icon(Icons.Default.Favorite, contentDescription = "Like")
    }
}
