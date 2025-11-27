/*
 * DiscoveryScreen.kt
 * Composable screen for swiping through discovery tracks and liking music.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.presentation.screens

//import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.ExperimentalMaterial3Api
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
import ca.sheridancollege.jamsy.presentation.components.PremiumButton
import ca.sheridancollege.jamsy.presentation.screens.discovery.DiscoveryContent
import ca.sheridancollege.jamsy.presentation.components.PremiumHeader
import ca.sheridancollege.jamsy.presentation.theme.SpotifyBlack
import ca.sheridancollege.jamsy.presentation.theme.SpotifyDarkGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.viewmodels.DiscoveryViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.LikedTracksViewModel
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
//@SuppressLint("AutoboxingStateCreation")
//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    onNavigateToGeneratedPlaylist: () -> Unit,
    onBack: () -> Unit,
    viewModel: DiscoveryViewModel,
    likedTracksViewModel: LikedTracksViewModel? = null,
    authToken: String = ""
) {
    val tracksState by viewModel.tracksState.collectAsState()
    val currentTrackIndex by viewModel.currentTrackIndex.collectAsState()
    val likedTracks by viewModel.likedTracks.collectAsState()
    val isNewSession by DiscoveryDataStore.isNewSession.collectAsState()
    
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isProcessingLike by remember { mutableStateOf(false) }
    var isProcessingDislike by remember { mutableStateOf(false) }
    
    // Initialize session and load tracks
    LaunchedEffect(authToken) {
        if (isNewSession) {
            viewModel.startNewDiscoverySession()
            DiscoveryDataStore.markSessionAsStarted()
        }
        
        if (authToken.isNotBlank()) {
            viewModel.loadDiscoveryTracks(authToken)
        } else {
            viewModel.loadBasicDiscoveryTracks()
        }
    }

    Scaffold(
        topBar = {
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
                PremiumHeader(
                    title = "Discover Music",
                    subtitle = "${likedTracks.size} tracks liked",
                    onBack = onBack,
                    animationDelay = 100,
                    showBackButton = true,
                    trailingContent = {
                        PremiumButton(
                            text = "View",
                            onClick = {
                                if (authToken.isNotBlank() && likedTracksViewModel != null) {
                                    likedTracksViewModel.loadLikedTracks(authToken)
                                }
                                onNavigateToGeneratedPlaylist()
                            },
                            enabled = likedTracks.isNotEmpty(),
                            fontSize = 12
                        )
                    }
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
                        onDragOffsetChange = { dragOffset = it },
                        onDragEnd = { offset ->
                            if (!isProcessingLike && !isProcessingDislike) {
                                val action = when {
                                    offset > SWIPE_THRESHOLD_LIKE -> "like"
                                    offset < SWIPE_THRESHOLD_DISLIKE -> "dislike"
                                    else -> null
                                }
                                
                                if (action != null) {
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
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
