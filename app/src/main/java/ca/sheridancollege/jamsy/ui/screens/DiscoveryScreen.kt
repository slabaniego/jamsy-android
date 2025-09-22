package ca.sheridancollege.jamsy.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.viewmodel.DiscoveryViewModel
import coil.compose.AsyncImage
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    onNavigateToLikedTracks: () -> Unit,
    onBack: () -> Unit,
    viewModel: DiscoveryViewModel
) {
    val tracksState by viewModel.tracksState.collectAsState()
    val currentTrackIndex by viewModel.currentTrackIndex.collectAsState()
    val likedTracks by viewModel.likedTracks.collectAsState()
    
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isProcessingAction by remember { mutableStateOf(false) }

    // Load discovery tracks when screen is shown
    LaunchedEffect(Unit) {
        // TODO: Get actual auth token from authentication system
        // For now, this will show authentication required error
        viewModel.loadDiscoveryTracks("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            "Discover Music",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${likedTracks.size} tracks liked",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = onNavigateToLikedTracks,
                        enabled = likedTracks.isNotEmpty()
                    ) {
                        Text("View Liked")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            when (val state = tracksState) {
                is Resource.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading discovery tracks...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                is Resource.Error -> {
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
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "To discover new tracks, please select your favorite artists first. This helps us recommend music you'll love!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onBack
                        ) {
                            Text("Select Artists")
                        }
                    }
                }

                is Resource.Success -> {
                    val tracks = state.data
                    if (tracks.isEmpty()) {
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
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "You've discovered all available tracks.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onNavigateToLikedTracks,
                                enabled = likedTracks.isNotEmpty()
                            ) {
                                Text("View Your Liked Tracks")
                            }
                        }
                    } else {
                        val currentTrack = viewModel.getCurrentTrack()
                        if (currentTrack != null) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                TrackCard(
                                    track = currentTrack,
                                    dragOffset = dragOffset,
                                    onDragEnd = { offset ->
                                        if (!isProcessingAction) {
                                            isProcessingAction = true
                                            val action = when {
                                                offset > 200 -> "like"
                                                offset < -200 -> "dislike"
                                                else -> null
                                            }
                                            
                                            if (action != null) {
                                                // TODO: Get actual auth token from authentication system
                                                viewModel.handleTrackAction(
                                                    track = currentTrack,
                                                    action = action,
                                                    authToken = "",
                                                    onComplete = {
                                                        isProcessingAction = false
                                                        dragOffset = 0f
                                                    }
                                                )
                                            } else {
                                                isProcessingAction = false
                                                dragOffset = 0f
                                            }
                                        }
                                    },
                                    onDragUpdate = { offset ->
                                        if (!isProcessingAction) {
                                            dragOffset = offset
                                        }
                                    }
                                )

                                // Action buttons
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(32.dp),
                                    horizontalArrangement = Arrangement.spacedBy(64.dp)
                                ) {
                                    FloatingActionButton(
                                        onClick = {
                                            if (!isProcessingAction) {
                                                isProcessingAction = true
                                                // TODO: Get actual auth token from authentication system
                                                viewModel.handleTrackAction(
                                                    track = currentTrack,
                                                    action = "dislike",
                                                    authToken = "",
                                                    onComplete = {
                                                        isProcessingAction = false
                                                    }
                                                )
                                            }
                                        },
                                        containerColor = if (isProcessingAction) 
                                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.errorContainer,
                                        contentColor = if (isProcessingAction)
                                            MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.onErrorContainer
                                    ) {
                                        Icon(Icons.Default.ThumbDown, contentDescription = "Dislike")
                                    }

                                    FloatingActionButton(
                                        onClick = {
                                            if (!isProcessingAction) {
                                                isProcessingAction = true
                                                // TODO: Get actual auth token from authentication system
                                                viewModel.handleTrackAction(
                                                    track = currentTrack,
                                                    action = "like",
                                                    authToken = "",
                                                    onComplete = {
                                                        isProcessingAction = false
                                                    }
                                                )
                                            }
                                        },
                                        containerColor = if (isProcessingAction)
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = if (isProcessingAction)
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.onPrimaryContainer
                                    ) {
                                        Icon(Icons.Default.Favorite, contentDescription = "Like")
                                    }
                                }

                                // Progress indicator
                                if (tracks.size > 1) {
                                    LinearProgressIndicator(
                                        progress = (currentTrackIndex + 1).toFloat() / tracks.size,
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackCard(
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
        animationSpec = tween(durationMillis = 100)
    )

    Card(
        modifier = Modifier
            .size(width = 300.dp, height = 500.dp)
            .graphicsLayer {
                translationX = dragOffset
                rotationZ = rotation
                alpha = 1f - (abs(dragOffset) / 800f)
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        onDragEnd(dragOffset)
                    }
                ) { _, dragAmount ->
                    onDragUpdate(dragOffset + dragAmount.x)
                }
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Album cover
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

                // Swipe direction indicators
                if (abs(dragOffset) > 50) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (dragOffset > 0) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
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
                            tint = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }

            // Track info
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
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = track.artists.joinToString(", "),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (track.genres.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        track.genres.take(2).forEach { genre ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = genre,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // Audio preview controls
                if (!track.previewUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (isPlaying) {
                                exoPlayer.pause()
                                isPlaying = false
                            } else {
                                exoPlayer.play()
                                isPlaying = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isPlaying) "⏸️ Pause Preview" else "▶️ Play Preview")
                    }
                }
            }
        }
    }
}