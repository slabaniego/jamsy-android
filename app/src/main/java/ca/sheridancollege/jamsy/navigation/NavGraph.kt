package ca.sheridancollege.jamsy.navigation

import androidx.compose.runtime.Composable
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

    val startDestination = remember {
        if (authViewModel.currentUser != null) Screen.Home.route else Screen.Login.route
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
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = profileViewModel
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = homeViewModel
            )
        }
    }
}