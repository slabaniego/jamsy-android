package ca.sheridancollege.jamsy.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import ca.sheridancollege.jamsy.domain.models.PlaylistTemplate
import ca.sheridancollege.jamsy.presentation.Screen
import ca.sheridancollege.jamsy.presentation.components.BottomBar
import ca.sheridancollege.jamsy.presentation.components.LoadingScreen
import ca.sheridancollege.jamsy.presentation.components.ErrorScreen
import ca.sheridancollege.jamsy.presentation.components.AppTopBar
import ca.sheridancollege.jamsy.presentation.viewmodels.PlaylistTemplateViewModel
import ca.sheridancollege.jamsy.presentation.theme.SpotifyMediumGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White
import ca.sheridancollege.jamsy.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistTemplateScreen(
    workout: String,
    onNavigateToHome: () -> Unit,
    onNavigateToArtistSelection: (String, String) -> Unit,
    onLogout: () -> Unit,
    viewModel: PlaylistTemplateViewModel,
    authToken: String?
) {
    val templatesState by viewModel.templatesState.collectAsState()

    // Load templates when screen is first shown
    LaunchedEffect(authToken) {
        authToken?.let { token ->
            viewModel.loadTemplates(token)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "$workout Playlists")
        },
        bottomBar = {
            BottomBar(
                selectedRoute = Screen.PlaylistTemplates.route,
                onHomeSelected = onNavigateToHome,
                onProfileSelected = { /* Not used in this screen */ },
                onTrackListSelected = { /* Not used in this screen */ },
                onLogoutSelected = onLogout
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            when (val state = templatesState) {
                is Resource.Loading -> {
                    LoadingScreen(message = "Loading workout playlists...")
                }

                is Resource.Error -> {
                    ErrorScreen(
                        title = "Failed to load templates",
                        message = state.message,
                        onRetry = {
                            authToken?.let { token ->
                                viewModel.loadTemplates(token)
                            }
                        }
                    )
                }

                is Resource.Success -> {
                    val allTemplates = state.data
                    val filteredTemplates = allTemplates.filter { template ->
                        template.name.equals(workout, ignoreCase = true) ||
                        (workout == "Strength Training" && template.name.equals("Weight Lifting", ignoreCase = true)) ||
                        (workout == "HIIT" && template.name.equals("HIIT", ignoreCase = true)) ||
                        (workout == "Yoga" && template.name.equals("Yoga Session", ignoreCase = true)) ||
                        (workout == "Cardio" && template.name.equals("Running", ignoreCase = true))
                    }
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(filteredTemplates) { template ->
                            PlaylistTemplateCard(
                                template = template,
                                onClick = {
                                    val mood = when (template.name) {
                                        "Cardio" -> "Energetic"
                                        "Strength Training", "Weight Lifting" -> "Powerful"
                                        "HIIT" -> "Intense"
                                        "Yoga", "Yoga Session" -> "Calm"
                                        "Running" -> "Motivated"
                                        else -> "Energetic"
                                    }
                                    onNavigateToArtistSelection(template.name, mood)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistTemplateCard(
    template: PlaylistTemplate,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = SpotifyMediumGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = SpotifyGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = template.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Text(
                            text = template.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Genres
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    template.seedGenres.take(3).forEach { genre ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SpotifyGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = genre,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = SpotifyGreen
                            )
                        }
                    }
                }
                
                // Tempo info
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tempo: ${template.targetTempo} BPM",
                    style = MaterialTheme.typography.labelMedium,
                    color = White.copy(alpha = 0.7f)
                )
            }
        }
    }
}
