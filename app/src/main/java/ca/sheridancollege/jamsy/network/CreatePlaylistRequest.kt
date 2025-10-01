package ca.sheridancollege.jamsy.network

import ca.sheridancollege.jamsy.model.Track
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatePlaylistRequest(
    @Json(name = "tracks")
    val tracks: List<Track>
)
