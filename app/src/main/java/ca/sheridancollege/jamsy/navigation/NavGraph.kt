package ca.sheridancollege.jamsy.navigation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ca.sheridancollege.jamsy.ui.screens.HomeScreen
import ca.sheridancollege.jamsy.ui.screens.LoginScreen
import ca.sheridancollege.jamsy.ui.screens.SignupScreen
import ca.sheridancollege.jamsy.ui.screens.ProfileScreen
import ca.sheridancollege.jamsy.ui.screens.TrackListScreen
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.viewmodel.AuthViewModel
import ca.sheridancollege.jamsy.viewmodel.HomeViewModel
import ca.sheridancollege.jamsy.viewmodel.ProfileViewModel
import ca.sheridancollege.jamsy.viewmodel.TrackListViewModel
import ca.sheridancollege.jamsy.viewmodel.ViewModelFactory

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Profile : Screen("profile")
    object Home : Screen("home")
    object TrackList : Screen("tracklist")
}

@Composable
fun NavGraph(navController: NavHostController) {

    val factory = ViewModelFactory()

    val authViewModel: AuthViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val profileViewModel: ProfileViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val homeViewModel: HomeViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val trackListViewModel: TrackListViewModel = viewModel(factory = factory, viewModelStoreOwner = LocalViewModelStoreOwner.current!!)

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
                onLogout = handleLogout,
                viewModel = homeViewModel
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
    }
}