package ca.sheridancollege.jamsy.presentation.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.presentation.swipe.SwipeCardConfig
import ca.sheridancollege.jamsy.presentation.swipe.SwipeTrackCard
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyBlack
import ca.sheridancollege.jamsy.presentation.theme.SpotifyDarkGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White
import ca.sheridancollege.jamsy.presentation.viewmodels.SwipeViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.SessionStats

/**
 * SwipeTrackScreen - Main screen for swiping to like/dislike tracks
 * Implements the complete Tinder-like swipe feature
 *
 * @param viewModel SwipeViewModel for state management
 * @param authToken Authentication token for API calls
 * @param onNavigateToResults Callback when all cards are swiped
 * @param onBack Callback to navigate back
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeTrackScreen(
    viewModel: SwipeViewModel,
    authToken: String,
    onNavigateToResults: (likedTracks: List<Track>) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val cards by viewModel.cards.collectAsState()
    val currentIndex by viewModel.currentCardIndex.collectAsState()
    val likedTracks by viewModel.likedTracks.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val error by viewModel.error.collectAsState()

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error in snackbar
    LaunchedEffect(error) {
        error?.let { errorMsg ->
            snackbarHostState.showSnackbar(errorMsg)
            viewModel.clearError()
        }
    }

    // Navigate to results when all cards are swiped
    LaunchedEffect(currentIndex, cards.size) {
        if (cards.isNotEmpty() && currentIndex >= cards.size - 1) {
            // Check if this is truly the last card
            if (currentIndex == cards.size - 1 && dragOffset == 0f) {
                // User has swiped through all cards
                onNavigateToResults(likedTracks)
            }
        }
    }

    val sessionStats = viewModel.getSessionStats()

    Scaffold(
        topBar = {
            SwipeScreenTopAppBar(
                stats = sessionStats,
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SpotifyDarkGray, SpotifyBlack)
                    )
                )
        ) {
            if (cards.isEmpty()) {
                EmptyState()
            } else if (currentIndex >= cards.size) {
                SessionCompleteState(
                    stats = sessionStats,
                    onViewResults = { onNavigateToResults(likedTracks) }
                )
            } else {
                SwipeCardContent(
                    track = cards[currentIndex],
                    dragOffset = dragOffset,
                    isProcessing = isProcessing,
                    onDragOffsetChange = { dragOffset = it },
                    onDragEnd = { offset ->
                        // Don't reset here, wait for action to complete
                    },
                    onLike = { track ->
                        viewModel.handleLike(track, authToken)
                        dragOffset = 0f
                    },
                    onDislike = { track ->
                        viewModel.handleDislike(track, authToken)
                        dragOffset = 0f
                    }
                )
            }
        }
    }
}

/**
 * Top app bar with progress and stats
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeScreenTopAppBar(
    stats: SessionStats,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SpotifyDarkGray)
    ) {
        TopAppBar(
            title = { 
                Column {
                    Text(
                        "Discover Tracks",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        "👍 ${stats.likedCount} • 👎 ${stats.dislikedCount}",
                        fontSize = 12.sp,
                        color = LightGray
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SpotifyGreen
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = SpotifyDarkGray)
        )

        // Progress bar
        LinearProgressIndicator(
            progress = stats.progressPercentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = SpotifyGreen,
            trackColor = LightGray.copy(alpha = 0.2f)
        )
    }
}

/**
 * Main swipe card content area
 */
@Composable
private fun SwipeCardContent(
    track: Track,
    dragOffset: Float,
    isProcessing: Boolean,
    onDragOffsetChange: (Float) -> Unit,
    onDragEnd: (Float) -> Unit,
    onLike: (Track) -> Unit,
    onDislike: (Track) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Swipe card with overlay for processing state
        Box(contentAlignment = Alignment.Center) {
            SwipeTrackCard(
                track = track,
                dragOffset = dragOffset,
                onDragUpdate = onDragOffsetChange,
                onDragEnd = onDragEnd,
                onLike = onLike,
                onDislike = onDislike,
                config = SwipeCardConfig(
                    rotationDegrees = 20f,
                    alphaThreshold = 400f,
                    swipeThreshold = 150f, // Increased for better mouse support
                    animationDurationMs = 200, // Longer animation for smoother feel
                    enableMouseSupport = true
                )
            )

            // Loading indicator overlay
            if (isProcessing) {
                Box(
                    modifier = Modifier
                        .size(320.dp, 480.dp)
                        .background(
                            color = SpotifyBlack.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SpotifyGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Action buttons
        SwipeActionButtons(
            onLike = { onLike(track) },
            onDislike = { onDislike(track) },
            enabled = !isProcessing
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Action buttons for like/dislike
 */
@Composable
private fun SwipeActionButtons(
    onLike: () -> Unit,
    onDislike: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dislike button
        IconButton(
            onClick = onDislike,
            enabled = enabled,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Dislike",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Like button
        IconButton(
            onClick = onLike,
            enabled = enabled,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = SpotifyGreen.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Like",
                tint = SpotifyGreen,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/**
 * Empty state when no cards available
 */
@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "No tracks available",
            style = MaterialTheme.typography.headlineSmall,
            color = White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Load some tracks to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = LightGray,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Session complete state showing final stats
 */
@Composable
private fun SessionCompleteState(
    stats: SessionStats,
    onViewResults: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "🎉 Session Complete!",
            style = MaterialTheme.typography.headlineMedium,
            color = SpotifyGreen,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Stats cards
        StatsGrid(stats = stats)

        Spacer(modifier = Modifier.height(32.dp))

        // View results button
        Button(
            onClick = onViewResults,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("View Your Likes", color = White, fontSize = 16.sp)
        }
    }
}

/**
 * Stats display grid
 */
@Composable
private fun StatsGrid(stats: SessionStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            label = "Liked",
            value = stats.likedCount.toString(),
            icon = "👍",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Disliked",
            value = stats.dislikedCount.toString(),
            icon = "👎",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Total",
            value = stats.totalCards.toString(),
            icon = "🎵",
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Individual stat card
 */
@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = SpotifyDarkGray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                icon,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                color = SpotifyGreen,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = LightGray
            )
        }
    }
}
