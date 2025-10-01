package ca.sheridancollege.jamsy.navigation
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.platform.LocalContext
import ca.sheridancollege.jamsy.ui.screens.HomeScreen
import ca.sheridancollege.jamsy.ui.screens.LoginScreen
import ca.sheridancollege.jamsy.ui.screens.SignupScreen
import ca.sheridancollege.jamsy.ui.screens.ProfileScreen
import ca.sheridancollege.jamsy.ui.screens.TrackListScreen
import ca.sheridancollege.jamsy.ui.screens.ChooseYourWorkoutScreen
import ca.sheridancollege.jamsy.ui.screens.PlaylistTemplateScreen
import ca.sheridancollege.jamsy.ui.screens.ArtistSelectionScreen
import ca.sheridancollege.jamsy.ui.screens.DiscoveryScreen
import ca.sheridancollege.jamsy.ui.screens.LikedTracksScreen
import ca.sheridancollege.jamsy.ui.screens.PlaylistCreationScreen
import ca.sheridancollege.jamsy.ui.screens.GeneratedPlaylistScreen
import ca.sheridancollege.jamsy.ui.screens.SearchScreen
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.viewmodel.AuthViewModel
import ca.sheridancollege.jamsy.viewmodel.HomeViewModel
import ca.sheridancollege.jamsy.viewmodel.ProfileViewModel
import ca.sheridancollege.jamsy.viewmodel.TrackListViewModel
import ca.sheridancollege.jamsy.viewmodel.GeneratedPlaylistViewModel
import ca.sheridancollege.jamsy.viewmodel.ViewModelFactory
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import ca.sheridancollege.jamsy.ui.screens.PlaylistPreviewScreen
import ca.sheridancollege.jamsy.viewmodel.PlaylistTemplateViewModel
import ca.sheridancollege.jamsy.viewmodel.ArtistSelectionViewModel
import ca.sheridancollege.jamsy.viewmodel.DiscoveryViewModel
import ca.sheridancollege.jamsy.viewmodel.LikedTracksViewModel
import ca.sheridancollege.jamsy.viewmodel.SearchViewModel


@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val factory = ViewModelFactory(context)

    val authViewModel: AuthViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val profileViewModel: ProfileViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val homeViewModel: HomeViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val trackListViewModel: TrackListViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val playlistTemplateViewModel: PlaylistTemplateViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val artistSelectionViewModel: ArtistSelectionViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val discoveryViewModel: DiscoveryViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val likedTracksViewModel: LikedTracksViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val searchViewModel: SearchViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val generatedPlaylistViewModel: GeneratedPlaylistViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)

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