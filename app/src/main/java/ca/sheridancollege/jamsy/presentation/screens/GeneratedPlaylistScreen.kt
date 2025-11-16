package ca.sheridancollege.jamsy.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.presentation.components.PremiumHeader
import ca.sheridancollege.jamsy.presentation.components.TrackItem
import ca.sheridancollege.jamsy.presentation.components.PremiumButton
import ca.sheridancollege.jamsy.presentation.components.GlassCard
import ca.sheridancollege.jamsy.presentation.screens.generated_playlist.GeneratedPlaylistLoadingState
import ca.sheridancollege.jamsy.presentation.screens.generated_playlist.GeneratedPlaylistEmptyState
import ca.sheridancollege.jamsy.presentation.screens.generated_playlist.GeneratedPlaylistErrorState
import ca.sheridancollege.jamsy.presentation.viewmodels.GeneratedPlaylistViewModel
import ca.sheridancollege.jamsy.presentation.theme.SpotifyBlack
import ca.sheridancollege.jamsy.presentation.theme.SpotifyDarkGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratedPlaylistScreen(
    onBack: () -> Unit,
    onExportToSpotify: () -> Unit,
    onRestartFlow: () -> Unit,
    viewModel: GeneratedPlaylistViewModel,
    authToken: String = ""
) {
    val playlistState by viewModel.playlistState.collectAsState()
    var isExporting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var playlistUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Load the generated playlist when the screen is composed
    LaunchedEffect(authToken) {
        if (authToken.isNotBlank()) {
            println("GeneratedPlaylistScreen: Loading generated playlist with authToken")
            viewModel.loadGeneratedPlaylist(authToken)
        } else {
            println("GeneratedPlaylistScreen: No authToken provided, loading without auth")
            viewModel.loadGeneratedPlaylist("")
        }
    }

    // Store tracks count for header
    val tracksCount = when (val state = playlistState) {
        is Resource.Success<List<Track>> -> state.data.size ?: 0
        else -> 0
    }

    Scaffold { paddingValues ->
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
            // Decorative gradient orbs for premium feel
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                SpotifyGreen.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            radius = 800f
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Premium header
                PremiumHeader(
                    title = "Generated Playlist",
                    subtitle = "$tracksCount tracks • ~${tracksCount * 3} minutes",
                    onBack = onBack,
                    onActionClick = {
                        viewModel.restartDiscoveryFlow()
                        onRestartFlow()
                    },
                    actionButtonText = "Start Over",
                    actionButtonEnabled = true,
                    animationDelay = 100
                )

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    when (val state = playlistState) {
                is Resource.Loading -> {
                    GeneratedPlaylistLoadingState()
                }

                is Resource.Success<List<Track>> -> {
                    val tracks = state.data
                    
                    if (tracks.isEmpty()) {
                        GeneratedPlaylistEmptyState(onBack = onBack)
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Header with premium glass effect
                            var headerAlpha by remember { mutableStateOf(0f) }
                            val animatedHeaderAlpha by animateFloatAsState(
                                targetValue = headerAlpha,
                                animationSpec = tween(durationMillis = 800, delayMillis = 300),
                                label = "header_alpha"
                            )
                            LaunchedEffect(Unit) { headerAlpha = 1f }
                            
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(animatedHeaderAlpha)
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = "Your Personalized Playlist",
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                        color = White
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = "${tracks.size} tracks • ~${tracks.size * 3} minutes",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = LightGray
                                    )
                                }
                            }

                            // Track list with weight to take remaining space
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(tracks) { track ->
                                    TrackItem(
                                        track = track,
                                        onTrackSelected = { /* Handle track selection if needed */ },
                                        onTrackAction = { /* Handle track actions if needed */ }
                                    )
                                }
                            }
                            
                            // Action buttons (fixed at bottom)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Export to Spotify button
                                PremiumButton(
                                    text = if (isExporting) "Creating Playlist..." else "Confirm and Export to Spotify",
                                    onClick = {
                                        if (authToken.isNotBlank()) {
                                            isExporting = true
                                            viewModel.exportToSpotify(
                                                authToken = authToken,
                                                onSuccess = { url ->
                                                    isExporting = false
                                                    playlistUrl = url
                                                    showSuccessDialog = true
                                                },
                                                onError = { error ->
                                                    isExporting = false
                                                    errorMessage = error
                                                    showErrorDialog = true
                                                }
                                            )
                                        } else {
                                            errorMessage = "Authentication required to export playlist"
                                            showErrorDialog = true
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isExporting,
                                    fontSize = 14
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    GeneratedPlaylistErrorState(
                        errorMessage = state.message,
                        onBack = onBack
                    )
                }
            }
        }
    }
    
    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Text(
                    text = "Playlist Created Successfully!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Your playlist has been exported to Spotify.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You can find it in your Spotify library.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onExportToSpotify() // Navigate back or to home
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
    
    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Text(
                    text = "Export Failed",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}} }


