package ca.sheridancollege.jamsy.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyAuthResponse(
    @Json(name = "access_token")
    val accessToken: String,

    @Json(name = "token_type")
    val tokenType: String,

    @Json(name = "firebaseCustomToken")
    val firebaseCustomToken: String
)