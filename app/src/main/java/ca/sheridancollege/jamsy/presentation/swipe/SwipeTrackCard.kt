package ca.sheridancollege.jamsy.presentation.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlin.math.abs
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyDarkGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White

private const val CARD_WIDTH = 320
private const val CARD_HEIGHT = 480
private const val IMAGE_HEIGHT = 280

/**
 * SwipeTrackCard - Specialized card for displaying music tracks with swipe gestures
 * Implements Liskov Substitution Principle - can be used wherever a swipeable card is expected
 *
 * @param track Track to display
 * @param dragOffset Current drag offset
 * @param onDragUpdate Called when drag updates
 * @param onDragEnd Called when drag ends
 * @param onLike Called when swiped right (like)
 * @param onDislike Called when swiped left (dislike)
 * @param modifier Modifier for styling
 * @param config Optional configuration for card behavior
 */
@Composable
fun SwipeTrackCard(
    track: Track,
    dragOffset: Float,
    onDragUpdate: (Float) -> Unit,
    onDragEnd: (Float) -> Unit,
    onLike: (Track) -> Unit,
    onDislike: (Track) -> Unit,
    modifier: Modifier = Modifier,
    config: SwipeCardConfig = SwipeCardConfig(
        rotationDegrees = 20f,
        alphaThreshold = 400f,
        swipeThreshold = 150f, // Better threshold for mouse support
        animationDurationMs = 200,
        enableMouseSupport = true
    )
) {
    SwipeableCard(
        modifier = modifier.size(width = CARD_WIDTH.dp, height = CARD_HEIGHT.dp),
        dragOffset = dragOffset,
        onDragUpdate = onDragUpdate,
        onDragEnd = onDragEnd,
        onSwipe = { direction ->
            when (direction) {
                SwipeDirection.RIGHT -> onLike(track)
                SwipeDirection.LEFT -> onDislike(track)
            }
        },
        config = config
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(containerColor = SpotifyDarkGray),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Album cover with swipe indicators
                TrackCardImageSection(
                    track = track,
                    dragOffset = dragOffset
                )

                // Track info section
                TrackCardInfoSection(track = track)
            }
        }
    }
}

/**
 * Album cover section with swipe direction indicators
 */
@Composable
private fun TrackCardImageSection(
    track: Track,
    dragOffset: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IMAGE_HEIGHT.dp),
        contentAlignment = Alignment.Center
    ) {
        // Album artwork
        AsyncImage(
            model = track.albumCover ?: track.imageUrl ?: "https://via.placeholder.com/300",
            contentDescription = "Album cover for ${track.name}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Swipe indicator overlay
        val (showLeft, showRight) = dragOffset.getSwipeIndicators(threshold = 50f)
        if (showLeft || showRight) {
            SwipeIndicator(
                dragOffset = dragOffset,
                showLeft = showLeft,
                showRight = showRight
            )
        }
    }
}

/**
 * Visual indicator showing like/dislike based on swipe direction
 */
@Composable
private fun SwipeIndicator(
    dragOffset: Float,
    showLeft: Boolean,
    showRight: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (dragOffset > 0) {
                    SpotifyGreen.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (dragOffset > 0) Icons.Default.Favorite else Icons.Default.ThumbDown,
            contentDescription = if (dragOffset > 0) "Like" else "Dislike",
            modifier = Modifier.size(64.dp),
            tint = White
        )
    }
}

/**
 * Track information section with title, artist, and genres
 */
@Composable
private fun TrackCardInfoSection(track: Track) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Track name
        Text(
            text = track.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = White
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Artist name
        Text(
            text = track.artists.joinToString(", "),
            style = MaterialTheme.typography.labelLarge,
            color = LightGray,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Genres
        if (!track.genres.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
            ) {
                track.genres.take(2).forEach { genre ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SpotifyGreen.copy(alpha = 0.3f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = genre,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = SpotifyGreen,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Popularity indicator
        Spacer(modifier = Modifier.height(8.dp))
        PopularityBar(popularity = track.popularity)
    }
}

/**
 * Visual popularity indicator
 */
@Composable
private fun PopularityBar(popularity: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = if (index < (popularity / 20)) SpotifyGreen else LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
