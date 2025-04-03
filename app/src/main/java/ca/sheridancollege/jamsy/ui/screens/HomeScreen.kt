package ca.sheridancollege.jamsy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import ca.sheridancollege.jamsy.navigation.Screen
import ca.sheridancollege.jamsy.ui.components.BottomBar
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.viewmodel.HomeViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToTrackList: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel
) {
    val tracksState by viewModel.tracksState.collectAsState()
    val currentTrackIndex by viewModel.currentTrackIndex.collectAsState()

    // Media player setup
    val context = LocalContext.current
    val exoPlayer = ExoPlayer.Builder(context).build()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover Music") }
            )
        },
        bottomBar = {
            BottomBar(
                selectedRoute = Screen.Home.route,
                onHomeSelected = { /* Already on home */ },
                onProfileSelected = onNavigateToProfile,
                onTrackListSelected = onNavigateToTrackList,
                onLogoutSelected = onLogout
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (tracksState) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }
                is Resource.Success -> {
                    val tracks = (tracksState as Resource.Success).data
                    if (tracks.isEmpty()) {
                        Text("No tracks available")
                    } else if (currentTrackIndex >= tracks.size) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("You're all done! 🎉")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.fetchTracksFromBackend() }) {
                                Text("Discover More Tracks")
                            }
                        }
                    } else {
                        val currentTrack = tracks[currentTrackIndex]

                        // Set up audio preview
                        if (currentTrack.previewUrl?.isNotEmpty() == true) {
                            exoPlayer.setMediaItem(MediaItem.fromUri(currentTrack.previewUrl))
                            exoPlayer.prepare()
                            exoPlayer.play()
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Album cover
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(currentTrack.albumCover)
                                            .crossfade(true)
                                            .build()
                                    ),
                                    contentDescription = "Album cover",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Track info
                            Text(
                                text = currentTrack.name,
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = currentTrack.artists.joinToString(", "),
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(8.dp))

//                            Text(
//                                text = "Genres: ${currentTrack.genres.joinToString(", ")}",
//                                style = MaterialTheme.typography.bodyMedium
//                            )

                            // Only show genres if available
                            currentTrack.genres?.let { genresList ->
                                Text(
                                    text = "Genres: ${genresList.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                            }


                            Spacer(modifier = Modifier.height(24.dp))

                            // Action buttons
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { viewModel.unlikeCurrentTrack() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Icon(Icons.Default.ThumbDown, contentDescription = "Unlike")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Skip")
                                }

                                Button(
                                    onClick = { viewModel.likeCurrentTrack() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = "Like")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Like")
                                }
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Error loading tracks: ${(tracksState as Resource.Error).message}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = { viewModel.fetchTracksFromBackend() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}