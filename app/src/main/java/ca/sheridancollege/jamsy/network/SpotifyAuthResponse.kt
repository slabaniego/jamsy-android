package ca.sheridancollege.jamsy.network

import com.google.gson.annotations.SerializedName

data class SpotifyAuthResponse(
    @SerializedName("access_token")
    val accessToken: String = "",

    @SerializedName("token_type")
    val tokenType: String = "",

    @SerializedName("firebaseCustomToken")
    val firebaseCustomToken: String = ""
)