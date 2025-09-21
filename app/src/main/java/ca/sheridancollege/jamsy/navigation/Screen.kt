package ca.sheridancollege.jamsy.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Search : Screen("search")
}
