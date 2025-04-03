package ca.sheridancollege.jamsy.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyAuthResponse(
    @Json(name = "access_token")
    val accessToken: String,

    @Json(name = "token_type")
    val tokenType: String,

    @Json(name = "firebaseCustomToken")
    val firebaseToken: String,

    @Json(name = "refresh_token")
    val refreshToken: String? = null,

    @Json(name = "expires_in")
    val expiresIn: Int? = null
)