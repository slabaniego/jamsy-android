package ca.sheridancollege.jamsy.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.sheridancollege.jamsy.navigation.Screen
import ca.sheridancollege.jamsy.ui.components.BottomBar
import ca.sheridancollege.jamsy.ui.components.TrackItem
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.viewmodel.TrackListViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button


@Composable
fun TrackListScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    onTrackSelected: (String) -> Unit,
    viewModel: TrackListViewModel
) {
    val trackListState by viewModel.tracksState.collectAsState()

    // Load tracks when the screen is first shown
    LaunchedEffect(Unit) {
        viewModel.loadTracks()
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                selectedRoute = Screen.TrackList.route,
                onHomeSelected = onNavigateToHome,
                onProfileSelected = onNavigateToProfile,
                onTrackListSelected = {},
                onLogoutSelected = onLogout
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = trackListState) {
                is Resource.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is Resource.Success -> {
                    val tracks = state.data ?: emptyList()
                    if (tracks.isEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text("No tracks available")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            items(tracks) { track ->
                                TrackItem(
                                    track = track,
                                    onClick = { track.id?.let { id -> onTrackSelected(id) } },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            val errorMessage = state.message

                            // Display a more user-friendly message
                            if (errorMessage.contains("Authentication required") ||
                                errorMessage.contains("log in")) {
                                Text(
                                    "You need to log in to your Spotify account first",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = onLogout
                                ) {
                                    Text("Go to Login")
                                }
                            } else {
                                Text(
                                    "Error: $errorMessage",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { viewModel.loadTracks() }
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}