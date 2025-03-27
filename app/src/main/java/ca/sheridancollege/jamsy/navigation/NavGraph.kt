package ca.sheridancollege.jamsy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ca.sheridancollege.jamsy.ui.screens.HomeScreen
import ca.sheridancollege.jamsy.ui.screens.LoginScreen
import ca.sheridancollege.jamsy.ui.screens.SignupScreen
import ca.sheridancollege.jamsy.ui.screens.ProfileScreen
import ca.sheridancollege.jamsy.viewmodel.AuthViewModel
import ca.sheridancollege.jamsy.viewmodel.HomeViewModel
import ca.sheridancollege.jamsy.viewmodel.ProfileViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Profile : Screen("profile")
    object Home : Screen("home")
}

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()

    // Remember the initial destination based on authentication state
    val startDestination = remember {
        if (authViewModel.currentUser != null) Screen.Home.route else Screen.Login.route
    }

    // ADDED: Monitor auth state changes to force navigation when user logs out
    LaunchedEffect(authViewModel.currentUser) {
        if (authViewModel.currentUser == null && navController.currentDestination?.route != Screen.Login.route) {
            // If user becomes null (logged out) and we're not already on login screen
            navController.navigate(Screen.Login.route) {
                // Clear the entire back stack
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // ADDED: Create a shared logout function to ensure consistent behavior
    val handleLogout: () -> Unit = {
        // First clear all view model state
        authViewModel.logout() // This now internally resets login/signup states

        // ADDED: Clear other viewmodels' states
        profileViewModel.clearUserData() // Added method to ProfileViewModel
        homeViewModel.clearData() // Added method to HomeViewModel

        // Then navigate to login screen with back stack cleared
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
                // CHANGED: Use the shared logout handler
                onLogout = handleLogout,
                viewModel = profileViewModel
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                // CHANGED: Use the shared logout handler
                onLogout = handleLogout,
                viewModel = homeViewModel
            )
        }
    }
}