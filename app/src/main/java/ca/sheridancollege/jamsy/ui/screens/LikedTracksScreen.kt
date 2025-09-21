package ca.sheridancollege.jamsy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.sheridancollege.jamsy.ui.components.TrackItem
import ca.sheridancollege.jamsy.ui.components.LoadingScreen
import ca.sheridancollege.jamsy.ui.components.ErrorScreen
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.viewmodel.LikedTracksViewModel
import ca.sheridancollege.jamsy.model.Track
// NOTE: If the import above does not resolve, change it to your actual Track package (e.g. ca.sheridancollege.jamsy.model.Track)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedTracksScreen(
    onBack: () -> Unit,
    viewModel: LikedTracksViewModel,
    authToken: String? = null,
    onPlaylistPreview: (() -> Unit)? = null,
    onExtendedPlaylistPreview: (() -> Unit)? = null
) {
    // Use collectAsState with an initial value and read via .value to avoid delegate/type inference issues
    val likedUiState = viewModel.likedTracksState.collectAsState(initial = Resource.Loading)
    
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf<String?>(null) }

    // Load liked tracks when screen is shown
    LaunchedEffect(authToken) { // Depend on authToken changes
        authToken?.let { token ->
            try {
                viewModel.loadLikedTracks(token)
            } catch (e: Exception) {
                showError = "Failed to load tracks: ${e.message}"
            }
        } ?: run {
            showError = "Authentication required"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Liked Tracks") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            when (val state = likedUiState.value) {
                is Resource.Success<*> -> {
                    val tracks: List<Track> = (state as Resource.Success<List<Track>>).data
                    if (tracks.isNotEmpty()) {
                        FloatingActionButton(
                            onClick = { showPlaylistDialog = true },
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
                else -> { /* no-op */ }
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
            when (val state = likedUiState.value) {
                is Resource.Loading -> {
                    LoadingScreen(message = "Loading your liked tracks...")
                }

                is Resource.Error -> {
                    ErrorScreen(
                        title = "Failed to load liked tracks",
                        message = state.message,
                        onRetry = {
                            authToken?.let { token ->
                                try {
                                    viewModel.loadLikedTracks(token)
                                } catch (_: Exception) {
                                    // Handle exception
                                }
                            }
                        }
                    )
                }

                is Resource.Success<*> -> {
                    val tracks: List<Track> = (state as Resource.Success<List<Track>>).data
                    if (tracks.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "❤️ No Liked Tracks Yet",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Start discovering music to build your collection!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Header with track count
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${tracks.size} track${if (tracks.size != 1) "s" else ""} liked",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    
                                    OutlinedButton(
                                        onClick = { onPlaylistPreview?.invoke() },
                                        modifier = Modifier.height(36.dp),
                                        enabled = onPlaylistPreview != null
                                    ) {
                                        Text("Preview Playlist", fontSize = 12.sp)
                                    }
                                }
                            }
                            
                            // Track list
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(items = tracks) { track ->
                                    TrackItem(
                                        track = track,
                                        onTrackSelected = { /* Handle track selection if needed */ },
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Playlist creation options dialog
    if (showPlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showPlaylistDialog = false },
            title = { Text("Create Playlist") },
            text = { Text("What would you like to do with your liked tracks?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPlaylistDialog = false
                        onExtendedPlaylistPreview?.invoke()
                    },
                    enabled = onExtendedPlaylistPreview != null
                ) {
                    Text("Preview Extended Playlist")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPlaylistDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Show error snackbar
    showError?.let { error ->
        LaunchedEffect(error) {
            // In a real app, you'd show a Snackbar here
            // For now, just clear the error after showing
            showError = null
        }
    }
}