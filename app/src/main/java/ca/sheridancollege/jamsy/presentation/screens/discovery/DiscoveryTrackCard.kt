package ca.sheridancollege.jamsy.presentation.screens.discovery

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

import coil.compose.AsyncImage

import kotlin.math.abs

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White

private const val DRAG_ANIMATION_DURATION = 100
private const val TRACK_CARD_WIDTH = 300
private const val TRACK_CARD_HEIGHT = 500

/**
 * Track card displaying album art, title, artist, and genres with drag interactions
 *
 * @param track Track to display
 * @param dragOffset Current drag offset for visual feedback
 * @param onDragEnd Callback when drag ends
 * @param onDragUpdate Callback for drag position updates
 */
@Composable
fun DiscoveryTrackCard(
    track: Track,
    dragOffset: Float,
    onDragEnd: (Float) -> Unit,
    onDragUpdate: (Float) -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(track.previewUrl) {
        if (!track.previewUrl.isNullOrBlank()) {
            val mediaItem = MediaItem.fromUri(track.previewUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
        onDispose {
            exoPlayer.release()
        }
    }

    val rotation by animateFloatAsState(
        targetValue = dragOffset * 0.1f,
        animationSpec = tween(durationMillis = DRAG_ANIMATION_DURATION)
    )

    Card(
        modifier = Modifier
            .size(width = TRACK_CARD_WIDTH.dp, height = TRACK_CARD_HEIGHT.dp)
            .graphicsLayer {
                translationX = dragOffset
                rotationZ = rotation
                alpha = 1f - (abs(dragOffset) / 800f)
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { onDragEnd(dragOffset) }
                ) { _, dragAmount ->
                    onDragUpdate(dragOffset + dragAmount.x)
                }
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Album cover with swipe indicators
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = track.albumCover ?: track.imageUrl ?: "https://via.placeholder.com/300",
                    contentDescription = "Album cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (abs(dragOffset) > 50) {
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
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = White
                        )
                    }
                }
            }

            // Track info
            TrackInfoContent(track = track, isPlaying = isPlaying, onPlayClick = {
                isPlaying = if (isPlaying) {
                    exoPlayer.pause()
                    false
                } else {
                    exoPlayer.play()
                    true
                }
            })
        }
    }
}

/**
 * Track information section including title, artist, genres, and preview controls
 */
@Composable
fun TrackInfoContent(
    track: Track,
    isPlaying: Boolean,
    onPlayClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = track.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = track.artists.joinToString(", "),
            style = MaterialTheme.typography.titleMedium,
            color = LightGray,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (!track.genres.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                track.genres.take(2).forEach { genre ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SpotifyGreen.copy(alpha = 0.3f),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
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

        // Audio preview controls
        if (!track.previewUrl.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onPlayClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen)
            ) {
                Text(if (isPlaying) "⏸️ Pause Preview" else "▶️ Play Preview", color = White)
            }
        }
    }
}
