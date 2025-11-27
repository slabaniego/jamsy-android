/*
 * PreviewPlaylistRequest.kt
 * Domain request model for preview playlist generation.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class PreviewPlaylistRequest(
    @Json(name = "likedTracks")
    val likedTracks: List<Track>
)

