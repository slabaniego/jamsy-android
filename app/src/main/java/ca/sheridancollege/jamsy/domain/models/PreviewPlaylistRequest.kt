package ca.sheridancollege.jamsy.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request body for preview playlist endpoint
 */
@JsonClass(generateAdapter = true)
data class PreviewPlaylistRequest(
    @Json(name = "likedTracks")
    val likedTracks: List<Track>
)

