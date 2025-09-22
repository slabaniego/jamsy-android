package ca.sheridancollege.jamsy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.sheridancollege.jamsy.model.PlaylistTemplate
import ca.sheridancollege.jamsy.navigation.Screen
import ca.sheridancollege.jamsy.ui.components.BottomBar
import ca.sheridancollege.jamsy.ui.components.LoadingScreen
import ca.sheridancollege.jamsy.ui.components.ErrorScreen
import ca.sheridancollege.jamsy.ui.components.AppTopBar
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.viewmodel.PlaylistTemplateViewModel

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
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                )
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = template.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = template.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Genres
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    template.genres.take(3).forEach { genre ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = genre,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                // Tempo info
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tempo: ${template.minTempo}-${template.maxTempo} BPM",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}
