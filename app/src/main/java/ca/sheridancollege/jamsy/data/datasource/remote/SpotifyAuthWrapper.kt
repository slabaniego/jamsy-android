package ca.sheridancollege.jamsy.data.datasource.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyAuthWrapper(
    @Json(name = "spotify")
    val spotify: SpotifyAuthResponse,
    
    @Json(name = "refresh_token")
    val refreshToken: String? = null
)
