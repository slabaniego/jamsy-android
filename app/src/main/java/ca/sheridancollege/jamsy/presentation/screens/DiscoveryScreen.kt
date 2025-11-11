package ca.sheridancollege.jamsy.presentation.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

import ca.sheridancollege.jamsy.data.DiscoveryDataStore
import ca.sheridancollege.jamsy.presentation.screens.discovery.DiscoveryContent
import ca.sheridancollege.jamsy.presentation.components.PremiumHeader
import ca.sheridancollege.jamsy.presentation.theme.SpotifyBlack
import ca.sheridancollege.jamsy.presentation.theme.SpotifyDarkGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.viewmodels.DiscoveryViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.LikedTracksViewModel

private const val TAG = "DiscoveryScreen"
private const val SWIPE_THRESHOLD_LIKE = 60
private const val SWIPE_THRESHOLD_DISLIKE = -60

/**
 * Discovery screen for discovering and rating music tracks
 *
 * @param onNavigateToGeneratedPlaylist Callback when navigating to generated playlist
 * @param onBack Callback when going back
 * @param viewModel DiscoveryViewModel for managing track state
 * @param likedTracksViewModel Optional ViewModel for liked tracks
 * @param authToken Authentication token for API calls
 */
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
    Log.d(TAG, "DiscoveryScreen composed, authToken length: ${authToken.length}")
    
    val tracksState by viewModel.tracksState.collectAsState()
    val currentTrackIndex by viewModel.currentTrackIndex.collectAsState()
    val likedTracks by viewModel.likedTracks.collectAsState()
    val isNewSession by DiscoveryDataStore.isNewSession.collectAsState()
    
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isProcessingLike by remember { mutableStateOf(false) }
    var isProcessingDislike by remember { mutableStateOf(false) }
    
    // Initialize session and load tracks
    LaunchedEffect(authToken) {
        Log.d(TAG, "LaunchedEffect triggered, is new session: $isNewSession")
        
        if (isNewSession) {
            Log.d(TAG, "New session detected - clearing liked tracks")
            viewModel.startNewDiscoverySession()
            DiscoveryDataStore.markSessionAsStarted()
        }
        
        if (authToken.isNotBlank()) {
            Log.d(TAG, "Loading discovery tracks with auth token")
            viewModel.loadDiscoveryTracks(authToken)
        } else {
            Log.d(TAG, "Loading basic discovery tracks without auth token")
            viewModel.loadBasicDiscoveryTracks()
        }
    }

    Scaffold(
        topBar = {
            // Premium header removed - integrated into main content for better animations
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SpotifyDarkGray, SpotifyBlack, SpotifyBlack)
                    )
                )
        ) {
            // Decorative gradient orbs for premium feel (similar to Login screen)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                SpotifyGreen.copy(alpha = 0.08f),
                                androidx.compose.ui.graphics.Color.Transparent
                            ),
                            radius = 800f
                        )
                    )
            )

            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Premium header at the top with animations
                PremiumHeader(
                    title = "Discover Music",
                    subtitle = "${likedTracks.size} tracks liked",
                    onBack = onBack,
                    onActionClick = {
                        if (authToken.isNotBlank() && likedTracksViewModel != null) {
                            likedTracksViewModel.loadLikedTracks(authToken)
                        }
                        onNavigateToGeneratedPlaylist()
                    },
                    actionButtonText = "View",
                    actionButtonEnabled = likedTracks.isNotEmpty(),
                    animationDelay = 100
                )

                // Main content area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    DiscoveryContent(
                        tracksState = tracksState,
                        currentTrackIndex = currentTrackIndex,
                        likedTracks = likedTracks,
                        dragOffset = dragOffset,
                        isProcessingLike = isProcessingLike,
                        isProcessingDislike = isProcessingDislike,
                        onDragOffsetChange = { dragOffset = it },
                        onProcessingLikeChange = { isProcessingLike = it },
                        onProcessingDislikeChange = { isProcessingDislike = it },
                        onDragEnd = { offset ->
                            if (!isProcessingLike && !isProcessingDislike) {
                                val action = when {
                                    offset > SWIPE_THRESHOLD_LIKE -> "like"
                                    offset < SWIPE_THRESHOLD_DISLIKE -> "dislike"
                                    else -> null
                                }
                                
                                if (action != null) {
                                    Log.d(TAG, "Processing swipe action: $action")
                                    val currentTrack = viewModel.getCurrentTrack()
                                    if (currentTrack != null) {
                                        if (action == "like") isProcessingLike = true else isProcessingDislike = true
                                        viewModel.handleTrackAction(
                                            track = currentTrack,
                                            action = action,
                                            authToken = authToken,
                                            onComplete = {
                                                isProcessingLike = false
                                                isProcessingDislike = false
                                                dragOffset = 0f
                                            }
                                        )
                                    }
                                } else {
                                    dragOffset = 0f
                                }
                            }
                        },
                        onNavigateToGeneratedPlaylist = onNavigateToGeneratedPlaylist,
                        onBack = onBack,
                        viewModel = viewModel,
                        authToken = authToken
                    )
                }
            }
        }
    }
}
