package ca.sheridancollege.jamsy.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ca.sheridancollege.jamsy.presentation.Screen
import ca.sheridancollege.jamsy.presentation.components.BottomBar
import ca.sheridancollege.jamsy.presentation.components.GlassCard
import ca.sheridancollege.jamsy.presentation.components.HomeActionCard
import ca.sheridancollege.jamsy.presentation.components.PremiumGradientButton
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyBlack
import ca.sheridancollege.jamsy.presentation.theme.SpotifyDarkGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White
import ca.sheridancollege.jamsy.presentation.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToTrackList: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToChooseWorkout: () -> Unit,
    onNavigateToDiscovery: () -> Unit,
    onLogout: () -> Unit,
    @Suppress("UNUSED_PARAMETER")
    viewModel: HomeViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover Music") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SpotifyDarkGray,
                    titleContentColor = SpotifyGreen
                )
            )
        },
        bottomBar = {
            BottomBar(
                selectedRoute = Screen.Home.route,
                onHomeSelected = { /* Already on home */ },
                onProfileSelected = onNavigateToProfile,
                onTrackListSelected = onNavigateToTrackList,
                onSearchSelected = onNavigateToSearch,
                onLogoutSelected = onLogout
            )
        }
    ) { paddingValues ->
        HomeScreenContent(
            paddingValues = paddingValues,
            onNavigateToChooseWorkout = onNavigateToChooseWorkout,
            onNavigateToDiscovery = onNavigateToDiscovery,
            onNavigateToSearch = onNavigateToSearch
        )
    }
}

@Composable
private fun HomeScreenContent(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    onNavigateToChooseWorkout: () -> Unit,
    onNavigateToDiscovery: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    // Animation states for entrance effects
    var titleAlpha by remember { mutableStateOf(0f) }
    var subtitleAlpha by remember { mutableStateOf(0f) }
    var mainButtonAlpha by remember { mutableStateOf(0f) }
    var actionCard1Alpha by remember { mutableStateOf(0f) }
    var actionCard2Alpha by remember { mutableStateOf(0f) }

    // Animated alpha values
    val animatedTitleAlpha by animateFloatAsState(
        targetValue = titleAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 100),
        label = "title_alpha"
    )
    val animatedSubtitleAlpha by animateFloatAsState(
        targetValue = subtitleAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 300),
        label = "subtitle_alpha"
    )
    val animatedMainButtonAlpha by animateFloatAsState(
        targetValue = mainButtonAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 500),
        label = "main_button_alpha"
    )
    val animatedActionCard1Alpha by animateFloatAsState(
        targetValue = actionCard1Alpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 700),
        label = "action_card1_alpha"
    )
    val animatedActionCard2Alpha by animateFloatAsState(
        targetValue = actionCard2Alpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 900),
        label = "action_card2_alpha"
    )

    LaunchedEffect(Unit) {
        titleAlpha = 1f
        subtitleAlpha = 1f
        mainButtonAlpha = 1f
        actionCard1Alpha = 1f
        actionCard2Alpha = 1f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SpotifyDarkGray,
                        SpotifyBlack,
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title + subtitle within glassmorphism card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animatedSubtitleAlpha)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Welcome to Jamsy",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(animatedTitleAlpha)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Discover music based on your workout and mood",
                        style = MaterialTheme.typography.bodyLarge,
                        color = LightGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Premium gradient main button
            PremiumGradientButton(
                text = "Start Music Discovery",
                onClick = onNavigateToChooseWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animatedMainButtonAlpha)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Discovery action card
            HomeActionCard(
                title = "Quick Discovery",
                //description = "Discover tracks instantly",
                onClick = onNavigateToDiscovery,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animatedActionCard1Alpha)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Music action card
            HomeActionCard(
                title = "Search Music",
               // description = "Find tracks, artists, or albums",
                onClick = onNavigateToSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animatedActionCard2Alpha)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
        paddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp),
        onNavigateToChooseWorkout = {},
        onNavigateToDiscovery = {},
        onNavigateToSearch = {}
    )
}