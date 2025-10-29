package ca.sheridancollege.jamsy.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import ca.sheridancollege.jamsy.presentation.screens.ArtistSelectionScreen
import ca.sheridancollege.jamsy.presentation.screens.ChooseYourWorkoutScreen
import ca.sheridancollege.jamsy.presentation.screens.DiscoveryScreen
import ca.sheridancollege.jamsy.presentation.screens.GeneratedPlaylistScreen
import ca.sheridancollege.jamsy.presentation.screens.HomeScreen
import ca.sheridancollege.jamsy.presentation.screens.LikedTracksScreen
import ca.sheridancollege.jamsy.presentation.screens.LoginScreen
import ca.sheridancollege.jamsy.presentation.screens.PlaylistCreationScreen
import ca.sheridancollege.jamsy.presentation.screens.PlaylistPreviewScreen
import ca.sheridancollege.jamsy.presentation.screens.PlaylistTemplateScreen
import ca.sheridancollege.jamsy.presentation.screens.ProfileScreen
import ca.sheridancollege.jamsy.presentation.screens.SearchScreen
import ca.sheridancollege.jamsy.presentation.screens.SignupScreen
import ca.sheridancollege.jamsy.presentation.screens.SwipeTrackScreen
import ca.sheridancollege.jamsy.presentation.screens.TrackListScreen
import ca.sheridancollege.jamsy.presentation.viewmodels.ArtistSelectionViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.AuthViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.DiscoveryViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.GeneratedPlaylistViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.HomeViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.LikedTracksViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.PlaylistTemplateViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.ProfileViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.SearchViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.SwipeViewModel
import ca.sheridancollege.jamsy.presentation.viewmodels.TrackListViewModel
import ca.sheridancollege.jamsy.util.Resource


@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val trackListViewModel: TrackListViewModel = viewModel()
    val playlistTemplateViewModel: PlaylistTemplateViewModel = viewModel()
    val artistSelectionViewModel: ArtistSelectionViewModel = viewModel()
    val discoveryViewModel: DiscoveryViewModel = viewModel()
    val swipeViewModel: SwipeViewModel = viewModel()
    val likedTracksViewModel: LikedTracksViewModel = viewModel()
    val searchViewModel: SearchViewModel = viewModel()
    val generatedPlaylistViewModel: GeneratedPlaylistViewModel = viewModel()

    val authState by authViewModel.authState.collectAsState()

    val startDestination = remember {
        if (authViewModel.currentUser != null) Screen.Home.route else Screen.Login.route
    }

    LaunchedEffect(authState) {
        when (authState) {
            is Resource.Success -> {
                val user = (authState as Resource.Success).data
                if (user == null) {

                    if (navController.currentDestination?.route != Screen.Login.route &&
                        navController.currentDestination?.route != Screen.Signup.route) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
            else -> {}
        }
    }


    val handleLogout: () -> Unit = {

        authViewModel.logout()
        profileViewModel.clearUserData()
        homeViewModel.clearData()
        trackListViewModel.clearData()

        navController.navigate(Screen.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignup = { navController.navigate(Screen.Signup.route) },
                onLoginSuccess = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }},
                viewModel = authViewModel
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onSignupSuccess = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Signup.route) { inclusive = true }
                }},
                viewModel = authViewModel
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onLogout = handleLogout,
                viewModel = profileViewModel
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToTrackList = { navController.navigate(Screen.TrackList.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToChooseWorkout = { navController.navigate(Screen.ChooseYourWorkout.route) },
                onNavigateToDiscovery = { navController.navigate(Screen.Discovery.route) },
                onLogout = handleLogout,
                viewModel = homeViewModel
            )
        }

        composable(Screen.ChooseYourWorkout.route) {
            ChooseYourWorkoutScreen(
                onWorkoutSelected = { workout, mood ->
                    navController.navigate("${Screen.ArtistSelection.route}/$workout/$mood")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TrackList.route) {
            TrackListScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onLogout = handleLogout,
                onTrackSelected = { trackId -> },
                viewModel = trackListViewModel
            )
        }

        composable("${Screen.PlaylistTemplates.route}/{workout}") { backStackEntry ->
            val workout = backStackEntry.arguments?.getString("workout") ?: ""
            
            PlaylistTemplateScreen(
                workout = workout,
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToArtistSelection = { selectedWorkout, mood -> 
                    navController.navigate("${Screen.ArtistSelection.route}/$selectedWorkout/$mood")
                },
                onLogout = handleLogout,
                viewModel = playlistTemplateViewModel,
                authToken = authViewModel.getSpotifyAccessToken()?.takeIf { it.isNotBlank() }
            )
        }

        composable("${Screen.ArtistSelection.route}/{workout}/{mood}") { backStackEntry ->
            val workout = backStackEntry.arguments?.getString("workout") ?: ""
            val mood = backStackEntry.arguments?.getString("mood") ?: ""
            
            ArtistSelectionScreen(
                workout = workout,
                mood = mood,
                onNavigateToDiscovery = { navController.navigate(Screen.Discovery.route) },
                onBack = { navController.popBackStack() },
                viewModel = artistSelectionViewModel,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Discovery.route) {
            val authToken = authViewModel.getSpotifyAccessToken()?.takeIf { it.isNotBlank() } ?: ""
            DiscoveryScreen(
                onNavigateToGeneratedPlaylist = { navController.navigate(Screen.GeneratedPlaylist.route) },
                onBack = { navController.popBackStack() },
                viewModel = discoveryViewModel,
                likedTracksViewModel = likedTracksViewModel,
                authToken = authToken
            )
        }

        composable(Screen.SwipeTrack.route) {
            val authToken = authViewModel.getSpotifyAccessToken()?.takeIf { it.isNotBlank() } ?: ""
            SwipeTrackScreen(
                viewModel = swipeViewModel,
                authToken = authToken,
                onNavigateToResults = { likedTracks ->
                    // Pass liked tracks to the next screen if needed
                    navController.navigate(Screen.GeneratedPlaylist.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LikedTracks.route) {
            LikedTracksScreen(
                onBack = { navController.popBackStack() },
                onPlaylistPreview = { navController.navigate(Screen.PlaylistPreview.route) },
                onExtendedPlaylistPreview = { navController.navigate(Screen.PlaylistCreation.route) },
                viewModel = likedTracksViewModel,
                authToken = authViewModel.getSpotifyAccessToken()?.takeIf { it.isNotBlank() }
            )
        }

        composable(Screen.PlaylistPreview.route) {
            PlaylistPreviewScreen(
                onNavigateToPlaylistCreation = { navController.navigate(Screen.PlaylistCreation.route) },
                onBack = { navController.popBackStack() },
                onRestartFlow = {
                    // Navigate back to home and clear the back stack
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                viewModel = likedTracksViewModel
            )
        }

        composable(Screen.PlaylistCreation.route) {
            PlaylistCreationScreen(
                onBack = { navController.popBackStack() },
                viewModel = likedTracksViewModel,
                authToken = authViewModel.getSpotifyAccessToken()?.takeIf { it.isNotBlank() }
            )
        }

        composable(Screen.GeneratedPlaylist.route) {
            val authToken = authViewModel.getSpotifyAccessToken()?.takeIf { it.isNotBlank() } ?: ""
            GeneratedPlaylistScreen(
                onBack = { navController.popBackStack() },
                onExportToSpotify = { 
                    // Navigate back to home after successful export
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onRestartFlow = {
                    // Navigate back to home and clear the back stack
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                viewModel = generatedPlaylistViewModel,
                authToken = authToken
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onLogout = handleLogout,
                viewModel = searchViewModel
            )
        }
    }
}