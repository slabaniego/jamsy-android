package ca.sheridancollege.jamsy.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.presentation.Screen
import ca.sheridancollege.jamsy.presentation.components.BottomBar
import ca.sheridancollege.jamsy.presentation.components.TrackItem
import ca.sheridancollege.jamsy.presentation.viewmodels.SearchViewModel
import ca.sheridancollege.jamsy.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SearchViewModel
) {
    val context = LocalContext.current
    
    val searchState by viewModel.searchState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var excludeExplicit by remember { mutableStateOf(true) }
    var excludeLoveSongs by remember { mutableStateOf(false) }
    var excludeFolk by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") }
            )
        },
        bottomBar = {
            BottomBar(
                selectedRoute = Screen.Search.route,
                onHomeSelected = onNavigateToHome,
                onProfileSelected = onNavigateToProfile,
                onLogoutSelected = onLogout
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            // Search header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Search Music",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Search field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search for tracks, artists, or albums") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { 
                                        searchQuery = ""
                                        viewModel.clearSearchResults()
                                    }
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    // Search button
                    Button(
                        onClick = {
                            if (searchQuery.isNotEmpty()) {
                                viewModel.searchTracks(
                                    query = searchQuery,
                                    excludeExplicit = excludeExplicit,
                                    excludeLoveSongs = excludeLoveSongs,
                                    excludeFolk = excludeFolk
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = searchQuery.isNotEmpty()
                    ) {
                        Text("Search")
                    }
                    
                    // Filter options
                    Column(
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = "Filters",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                onClick = { excludeExplicit = !excludeExplicit },
                                label = { Text("No Explicit") },
                                selected = excludeExplicit
                            )
                            FilterChip(
                                onClick = { excludeLoveSongs = !excludeLoveSongs },
                                label = { Text("No Love Songs") },
                                selected = excludeLoveSongs
                            )
                            FilterChip(
                                onClick = { excludeFolk = !excludeFolk },
                                label = { Text("No Folk") },
                                selected = excludeFolk
                            )
                        }
                    }
                }
            }
            
            // Results area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when (val state = searchState) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    
                    is Resource.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Error: ${state.message}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { 
                                if (searchQuery.isNotBlank()) {
                                    viewModel.searchTracks(
                                        query = searchQuery,
                                        excludeExplicit = excludeExplicit,
                                        excludeLoveSongs = excludeLoveSongs,
                                        excludeFolk = excludeFolk
                                    )
                                }
                            }) {
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
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "🔍",
                                    style = MaterialTheme.typography.displayMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No tracks found",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Try adjusting your search or filters",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    Text(
                                        text = "${tracks.size} result${if (tracks.size != 1) "s" else ""} found",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                
                                items(tracks) { track ->
                                    TrackItem(
                                        track = track,
                                        onTrackSelected = { /* Handle track selection */ },
                                        onTrackAction = { /* Handle track action if needed */ }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}