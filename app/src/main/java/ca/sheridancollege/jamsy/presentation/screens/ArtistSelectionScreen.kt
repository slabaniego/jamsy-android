package ca.sheridancollege.jamsy.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ca.sheridancollege.jamsy.domain.models.Artist
import ca.sheridancollege.jamsy.presentation.viewmodels.ArtistSelectionViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.AuthViewModel
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.presentation.components.GlassCard
import ca.sheridancollege.jamsy.presentation.components.PremiumArtistCard
import ca.sheridancollege.jamsy.presentation.components.PremiumGradientButton
import ca.sheridancollege.jamsy.presentation.theme.SpotifyBlack
import ca.sheridancollege.jamsy.presentation.theme.SpotifyDarkGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White
import ca.sheridancollege.jamsy.presentation.theme.LightGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistSelectionScreen(
    workout: String,
    mood: String,
    onNavigateToDiscovery: () -> Unit,
    onBack: () -> Unit,
    viewModel: ArtistSelectionViewModel,
    authViewModel: AuthViewModel
) {
    val artistsState by viewModel.artistsState.collectAsState()
    val selectedArtists by viewModel.selectedArtists.collectAsState()
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Load artists when screen is shown
    LaunchedEffect(workout, mood) {
        val currentUser = authViewModel.currentUser
        println("ArtistSelectionScreen: currentUser = ${currentUser?.uid}")
        println("ArtistSelectionScreen: currentUser is null = ${currentUser == null}")
        
        // Check if user is authenticated with Firebase first
        if (currentUser == null) {
            println("ArtistSelectionScreen: No Firebase user, showing error")
            viewModel.setErrorState("Please log in first")
            return@LaunchedEffect
        }
        
        val authToken = authViewModel.getSpotifyAccessToken()?.takeIf { it.isNotBlank() }
        if (authToken == null) {
            println("ArtistSelectionScreen: No valid Spotify token available, showing error")
            viewModel.setErrorState("Please log in with Spotify first")
            return@LaunchedEffect
        }
        
        println("ArtistSelectionScreen: authToken = real token: ${authToken.take(10)}...")
        println("ArtistSelectionScreen: authToken length = ${authToken.length}")
        viewModel.loadArtists(workout, mood, authToken)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            "Select Artists",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Text(
                            "$workout • $mood",
                            fontSize = 12.sp,
                            color = LightGray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SpotifyDarkGray,
                    titleContentColor = SpotifyGreen,
                    navigationIconContentColor = White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = SpotifyDarkGray
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${selectedArtists.size}/5 artists selected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedArtists.size == 5) SpotifyGreen else LightGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    PremiumGradientButton(
                        text = "Start Discovering New Music",
                        onClick = {
                            val authToken = authViewModel.getSpotifyAccessToken()?.takeIf { it.isNotBlank() } ?: "dummy_token"
                            viewModel.submitSelection(
                                workout = workout,
                                mood = mood,
                                action = "discover",
                                authToken = authToken,
                                onSuccess = onNavigateToDiscovery,
                                onError = { error ->
                                    errorMessage = error
                                    showError = true
                                }
                            )
                        },
                        enabled = selectedArtists.size == 5
                    )
                }
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
                            SpotifyDarkGray,
                            SpotifyBlack,
                            SpotifyBlack
                        )
                    )
                )
        ) {
            // Decorative green glow
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

            when (val state = artistsState) {
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
                                text = "Finding artists for $workout...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LightGray
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
                            text = "Failed to load artists",
                            style = MaterialTheme.typography.headlineSmall,
                            color = SpotifyGreen
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val authToken = authViewModel.getSpotifyAccessToken()?.takeIf { it.isNotBlank() } ?: "dummy_token"
                                viewModel.loadArtists(workout, mood, authToken)
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }

                is Resource.Success -> {
                    val artists = state.data
                    if (artists.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No artists found",
                                style = MaterialTheme.typography.headlineSmall,
                                color = White
                            )
                            Text(
                                text = "Try a different workout type",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LightGray
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Header with glass card
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Choose artists you like to personalize your playlist",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = LightGray,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                            
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(artists) { artist ->
                                    PremiumArtistCard(
                                        artist = artist,
                                        isSelected = selectedArtists.contains(artist),
                                        onToggle = { viewModel.toggleArtistSelection(artist) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Error dialog
    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showError = false }) {
                    Text("OK")
                }
            }
        )
    }
}
