package ca.sheridancollege.jamsy.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiscoveryRequest(
    @Json(name = "seedArtists")
    val seedArtists: List<String>,
    
    @Json(name = "workout")
    val workout: String
)
