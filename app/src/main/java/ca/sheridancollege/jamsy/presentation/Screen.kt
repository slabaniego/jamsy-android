package ca.sheridancollege.jamsy.presentation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Profile : Screen("profile")
    object Home : Screen("home")
    object ChooseYourWorkout : Screen("choose_workout")
    object ArtistSelection : Screen("artist_selection")
    object Discovery : Screen("discovery")
    object GeneratedPlaylist : Screen("generated_playlist")
}
