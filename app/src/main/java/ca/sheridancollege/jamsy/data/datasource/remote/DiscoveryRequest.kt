package ca.sheridancollege.jamsy.data.datasource.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiscoveryRequest(
    @Json(name = "seedArtists")
    val seedArtists: List<String>,
    
    @Json(name = "workout")
    val workout: String
)
