package ca.sheridancollege.jamsy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ca.sheridancollege.jamsy.ui.components.TrackItem
import ca.sheridancollege.jamsy.ui.components.AppTopBar
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.viewmodel.LikedTracksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistPreviewScreen(
    onNavigateToPlaylistCreation: () -> Unit,
    onBack: () -> Unit,
    viewModel: LikedTracksViewModel
) {
    val playlistPreviewState by viewModel.playlistPreviewState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    // Load playlist preview when screen is shown
    LaunchedEffect(Unit) {
        val authToken = "dummy_token" // Replace with actual token
        viewModel.previewPlaylist(authToken)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Playlist Preview",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        floatingActionButton = {
            when (val state = playlistPreviewState) {
                is Resource.Success -> {
                    val tracks = state.data
                    if (tracks.isNotEmpty()) {
                        FloatingActionButton(
                            onClick = { showCreateDialog = true },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.PlaylistPlay,
                                contentDescription = "Create Playlist",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            when (val state = playlistPreviewState) {
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
                                text = "Generating your playlist...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Finding similar tracks to expand your selection",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
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
                            text = "Failed to generate playlist",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val authToken = "dummy_token" // Replace with actual token
                                viewModel.previewPlaylist(authToken)
                            }
                        ) {
                            Text("Retry")
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
                                text = "No Playlist Generated",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "You need to like some tracks first to generate a playlist",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onBack) {
                                Text("Go Back")
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Playlist info header
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    Text(
                                        text = "🎶 Your Extended Playlist",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${tracks.size} tracks • ~${(tracks.size * 3.5).toInt()} minutes",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "We've expanded your liked tracks with similar music for the perfect playlist",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            
                            // Track list
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(tracks.withIndex().toList()) { indexedTrack ->
                                    val (index, track) = indexedTrack
                                    TrackItem(
                                        track = track,
                                        onTrackSelected = { /* Handle track selection if needed */ },
                                        onTrackAction = { /* Handle track action if needed */ },
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        showIndex = true,
                                        index = index + 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create playlist confirmation dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Spotify Playlist") },
            text = { 
                Text("This will create a new playlist in your Spotify account with all these tracks. Continue?") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCreateDialog = false
                        onNavigateToPlaylistCreation()
                    }
                ) {
                    Text("Create Playlist")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}