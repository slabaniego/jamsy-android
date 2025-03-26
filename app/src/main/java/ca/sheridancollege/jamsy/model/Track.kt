package ca.sheridancollege.jamsy.model

data class Track(
    val id: String = "",
    val title: String = "",
    val artist: String = "",
    val albumArt: String = "",
    val duration: Long = 0
)