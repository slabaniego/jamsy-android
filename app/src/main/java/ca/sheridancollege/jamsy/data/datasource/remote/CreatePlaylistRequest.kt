package ca.sheridancollege.jamsy.data.datasource.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

import ca.sheridancollege.jamsy.domain.models.Track

@JsonClass(generateAdapter = true)
data class CreatePlaylistRequest(
    @Json(name = "tracks")
    val tracks: List<Track>,
    
    @Json(name = "playlistName")
    val playlistName: String = "My Mobile Playlist"
)
