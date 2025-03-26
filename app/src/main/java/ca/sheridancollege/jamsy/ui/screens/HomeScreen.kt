package ca.sheridancollege.jamsy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.sheridancollege.jamsy.navigation.Screen
import ca.sheridancollege.jamsy.ui.components.BottomBar
import ca.sheridancollege.jamsy.ui.components.TrackItem
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel
) {
    val tracksState by viewModel.tracksState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home - Tracks") }
            )
        },
        bottomBar = {
            BottomBar(
                selectedRoute = Screen.Home.route,
                onHomeSelected = { /* Already on home */ },
                onProfileSelected = onNavigateToProfile,
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        items(tracks) { track ->
                            TrackItem(
                                track = track,
                                onClick = {
                                    // Handle track click, e.g. play music or show details
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
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

                        Button(onClick = { /* Retry loading tracks */ }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}