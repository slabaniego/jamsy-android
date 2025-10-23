package ca.sheridancollege.jamsy.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

import coil.compose.AsyncImage

import kotlin.math.abs

import ca.sheridancollege.jamsy.data.DiscoveryDataStore
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.presentation.viewmodels.DiscoveryViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.LikedTracksViewModel
import ca.sheridancollege.jamsy.util.Resource

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    onNavigateToGeneratedPlaylist: () -> Unit,
    onBack: () -> Unit,
    viewModel: DiscoveryViewModel,
    likedTracksViewModel: LikedTracksViewModel? = null,
    authToken: String = ""
) {
    println("DiscoveryScreen: ===== DISCOVERY SCREEN COMPOSED =====")
    println("DiscoveryScreen: AuthToken length: ${authToken.length}")
    
    val tracksState by viewModel.tracksState.collectAsState()
    val currentTrackIndex by viewModel.currentTrackIndex.collectAsState()
    val likedTracks by viewModel.likedTracks.collectAsState()
    val isNewSession by DiscoveryDataStore.isNewSession.collectAsState()
    
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isProcessingAction by remember { mutableStateOf(false) }
    var lastActionTime by remember { mutableLongStateOf(0L) }
    
    // Initialize session and load tracks only once
    LaunchedEffect(authToken) {
        println("DiscoveryScreen: ===== LaunchedEffect triggered =====")
        println("DiscoveryScreen: AuthToken length: ${authToken.length}")
        println("DiscoveryScreen: Is new session: $isNewSession")
        
        if (isNewSession) {
            println("DiscoveryScreen: New session detected - clearing liked tracks")
            viewModel.startNewDiscoverySession()
            DiscoveryDataStore.markSessionAsStarted()
        } else {
            println("DiscoveryScreen: Existing session - preserving liked tracks")
            // Don't reset the viewModel for existing sessions to preserve state
        }
        
        // Load tracks
        if (authToken.isNotBlank()) {
            println("DiscoveryScreen: Loading discovery tracks with auth token")
            viewModel.loadDiscoveryTracks(authToken)
        } else {
            println("DiscoveryScreen: Loading basic discovery tracks without auth token")
            viewModel.loadBasicDiscoveryTracks()
        }
    }
    
    // Debug logging for currentTrackIndex changes
    LaunchedEffect(currentTrackIndex) {
        println("DiscoveryScreen: currentTrackIndex changed to: $currentTrackIndex")
    }
    
    // Debug logging for tracks state
    LaunchedEffect(tracksState) {
        val currentState = tracksState
        when (currentState) {
            is Resource.Loading -> println("DiscoveryScreen: Tracks state is Loading")
            is Resource.Error -> println("DiscoveryScreen: Tracks state is Error: ${currentState.message}")
            is Resource.Success -> {
                println("DiscoveryScreen: Tracks state is Success with ${currentState.data.size} tracks")
                println("DiscoveryScreen: Track names: ${currentState.data.map { "${it.name} by ${it.artists.firstOrNull()}" }}")
            }
        }
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
                        onClick = {
                            // Refresh liked tracks from server before navigating
                            if (authToken.isNotBlank() && likedTracksViewModel != null) {
                                likedTracksViewModel.loadLikedTracks(authToken)
                            }
                            onNavigateToGeneratedPlaylist()
                        },
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
                    println("DiscoveryScreen: Resource.Success - tracks count: ${tracks.size}")
                    println("DiscoveryScreen: currentTrackIndex: $currentTrackIndex")
                    
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
                                onClick = onNavigateToGeneratedPlaylist,
                                enabled = likedTracks.isNotEmpty()
                            ) {
                                Text("View Your Liked Tracks")
                            }
                        }
                    } else {
                        val currentTrack = viewModel.getCurrentTrack()
                        val actualIndex = viewModel.currentTrackIndex.value
                        println("DiscoveryScreen: getCurrentTrack() returned: $currentTrack")
                        if (currentTrack != null) {
                            println("DiscoveryScreen: ✅ SUCCESS! Displaying track: ${currentTrack.name} by ${currentTrack.artists.firstOrNull()}")
                            println("DiscoveryScreen: ✅ Track index: $actualIndex (should be 0)")
                            println("DiscoveryScreen: About to render TrackCard with like/dislike buttons")
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
                                            
                                            println("DiscoveryScreen: Swipe action detected - offset: $offset, action: $action")
                                            
                                            if (action != null) {
                                                println("DiscoveryScreen: Processing $action for track: ${currentTrack.name} by ${currentTrack.artists.firstOrNull()}")
                                                viewModel.handleTrackAction(
                                                    track = currentTrack,
                                                    action = action,
                                                    authToken = authToken,
                                                    onComplete = {
                                                        println("DiscoveryScreen: Track action completed for ${currentTrack.name}")
                                                        isProcessingAction = false
                                                        dragOffset = 0f
                                                    }
                                                )
                                            } else {
                                                println("DiscoveryScreen: No action triggered - offset not sufficient: $offset")
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
                                            val currentTime = System.currentTimeMillis()
                                            println("DiscoveryScreen: ===== DISLIKE BUTTON CLICKED =====")
                                            println("DiscoveryScreen: Track: ${currentTrack.name} by ${currentTrack.artists.firstOrNull()}")
                                            println("DiscoveryScreen: Time since last action: ${currentTime - lastActionTime}ms")
                                            
                                            // Always process the action for now to debug
                                            println("DiscoveryScreen: Processing dislike action for track: ${currentTrack.name}")
                                            lastActionTime = currentTime
                                            isProcessingAction = true
                                            viewModel.handleTrackAction(
                                                track = currentTrack,
                                                action = "dislike",
                                                authToken = authToken,
                                                onComplete = {
                                                    println("DiscoveryScreen: Dislike action completed for ${currentTrack.name}")
                                                    isProcessingAction = false
                                                }
                                            )
                                        },
                                        modifier = Modifier
                                            .size(64.dp),
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
                                            println("DiscoveryScreen: ===== LIKE BUTTON CLICKED =====")
                                            val currentTime = System.currentTimeMillis()
                                            println("DiscoveryScreen: Track: ${currentTrack.name} by ${currentTrack.artists.firstOrNull()}")
                                            println("DiscoveryScreen: isProcessingAction: $isProcessingAction")
                                            println("DiscoveryScreen: Current liked tracks count: ${likedTracks.size}")
                                            println("DiscoveryScreen: Time since last action: ${currentTime - lastActionTime}ms")
                                            println("DiscoveryScreen: AuthToken length: ${authToken.length}")
                                            
                                            // Always process the action for now to debug
                                            println("DiscoveryScreen: Processing like action for track: ${currentTrack.name}")
                                            lastActionTime = currentTime
                                            isProcessingAction = true
                                            viewModel.handleTrackAction(
                                                track = currentTrack,
                                                action = "like",
                                                authToken = authToken,
                                                onComplete = {
                                                    println("DiscoveryScreen: Like action completed for ${currentTrack.name}")
                                                    isProcessingAction = false
                                                }
                                            )
                                        },
                                        modifier = Modifier
                                            .size(64.dp),
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

                                // View Generated Playlist button (appears after all tracks are processed)
                                if (currentTrackIndex >= tracks.size - 1) {
                                    Button(
                                        onClick = onNavigateToGeneratedPlaylist,
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(
                                            "View Generated Playlist",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
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

                if (!track.genres.isNullOrEmpty()) {
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