package ca.sheridancollege.jamsy.api


data class TrackAction(
    val isrc: String,
    val songName: String,
    val artist: List<String>,
    val genres: List<String>,
    val action: String // "like" or "unlike"
)