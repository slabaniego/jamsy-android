package ca.sheridancollege.jamsy.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ca.sheridancollege.jamsy.presentation.components.GlassCard
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyBlack
import ca.sheridancollege.jamsy.presentation.theme.SpotifyDarkGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.SpotifyMediumGray
import ca.sheridancollege.jamsy.presentation.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseYourWorkoutScreen(
    onWorkoutSelected: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val workoutOptions = listOf(
        WorkoutOption("Cardio", "💓", "High-energy cardio workouts", "Energetic"),
        WorkoutOption("Strength", "💪", "Build muscle and strength", "Powerful"),
        WorkoutOption("HIIT", "⚡", "High-intensity interval training", "Intense"),
        WorkoutOption("Yoga", "🧘", "Mindful movement and flexibility", "Calm")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Choose Your Workout",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpotifyGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SpotifyGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SpotifyDarkGray
                )
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
                            SpotifyDarkGray,
                            SpotifyBlack
                        )
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "What type of workout are you planning?",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Select a workout type to get personalized music recommendations",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
                ) {
                    items(workoutOptions) { workout ->
                        WorkoutCard(
                            workout = workout,
                            onClick = { onWorkoutSelected(workout.name, workout.mood) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutCard(
    workout: WorkoutOption,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = workout.emoji,
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = workout.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

data class WorkoutOption(
    val name: String,
    val emoji: String,
    val description: String,
    val mood: String
)
