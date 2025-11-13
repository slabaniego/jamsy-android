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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import ca.sheridancollege.jamsy.presentation.Screen
import ca.sheridancollege.jamsy.presentation.components.BottomBar
import ca.sheridancollege.jamsy.presentation.components.GlassCard
import ca.sheridancollege.jamsy.presentation.components.PremiumButton
import ca.sheridancollege.jamsy.presentation.components.PremiumHeader
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyBlack
import ca.sheridancollege.jamsy.presentation.theme.SpotifyDarkGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White
import ca.sheridancollege.jamsy.presentation.viewmodels.HomeViewModel
import ca.sheridancollege.jamsy.domain.models.User
import ca.sheridancollege.jamsy.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToTrackList: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToChooseWorkout: () -> Unit,
    onNavigateToDiscovery: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel
) {
    val userProfileState by viewModel.userProfileState.collectAsState()

    Scaffold(
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
            userProfileState = userProfileState,
            onNavigateToChooseWorkout = onNavigateToChooseWorkout,
            onNavigateToDiscovery = onNavigateToDiscovery,
            onNavigateToSearch = onNavigateToSearch
        )
    }
}

@Composable
private fun HomeScreenContent(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    userProfileState: Resource<User>,
    onNavigateToChooseWorkout: () -> Unit,
    onNavigateToDiscovery: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    // Animation states for entrance effects
    var welcomeAlpha by remember { mutableStateOf(0f) }
    var mainButtonAlpha by remember { mutableStateOf(0f) }
    var actionCard1Alpha by remember { mutableStateOf(0f) }
    var actionCard2Alpha by remember { mutableStateOf(0f) }

    // Animated alpha values
    val animatedWelcomeAlpha by animateFloatAsState(
        targetValue = welcomeAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 200),
        label = "welcome_alpha"
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
        welcomeAlpha = 1f
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
        ) {
            // Premium header with user avatar
            val spotifyImageUrl = (userProfileState as? Resource.Success)?.data?.spotifyProfileImageUrl
            val localImageBase64 = (userProfileState as? Resource.Success)?.data?.profileImageBase64
            val isLoadingImage = userProfileState is Resource.Loading

            PremiumHeader(
                title = "Discover Music",
                subtitle = "Discover music based on your\nworkout and mood",
                onBack = { /* No back action */ },
                onActionClick = { /* No action */ },
                actionButtonText = "",
                actionButtonEnabled = false,
                animationDelay = 100,
                showBackButton = false,
                spotifyImageUrl = spotifyImageUrl,
                localImageBase64 = localImageBase64,
                isLoadingImage = isLoadingImage
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Welcome card with glassmorphism
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(animatedWelcomeAlpha)
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
                            style = MaterialTheme.typography.headlineMedium,
                            color = White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Discover music based on your workout and mood",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Premium gradient main button
                PremiumButton(
                    text = "Start Music Discovery",
                    onClick = onNavigateToChooseWorkout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(animatedMainButtonAlpha),
                    enabled = true,
                    fontSize = 14
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Discovery button
                PremiumButton(
                    text = "Quick Discovery",
                    onClick = onNavigateToDiscovery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(animatedActionCard1Alpha),
                    enabled = true,
                    fontSize = 14
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Search Music button
                PremiumButton(
                    text = "Search Music",
                    onClick = onNavigateToSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(animatedActionCard2Alpha),
                    enabled = true,
                    fontSize = 14
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
        paddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp),
        userProfileState = Resource.Loading,
        onNavigateToChooseWorkout = {},
        onNavigateToDiscovery = {},
        onNavigateToSearch = {}
    )
}