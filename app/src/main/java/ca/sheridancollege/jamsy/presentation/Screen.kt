package ca.sheridancollege.jamsy.presentation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Profile : Screen("profile")
    object Home : Screen("home")
    object TrackList : Screen("tracklist")
    object ChooseYourWorkout : Screen("choose_workout")
    object PlaylistTemplates : Screen("playlist_templates")
    object ArtistSelection : Screen("artist_selection")
    object Discovery : Screen("discovery")
    object LikedTracks : Screen("liked_tracks")
    object PlaylistPreview : Screen("playlist_preview")
    object PlaylistCreation : Screen("playlist_creation")
    object GeneratedPlaylist : Screen("generated_playlist")
    object Search : Screen("search")
}
