package ca.sheridancollege.jamsy.data.datasource.remote

/**
 * Response model for Spotify User Profile API endpoint
 * GET https://api.spotify.com/v1/me
 */
data class SpotifyUserResponse(
    val id: String = "",
    val display_name: String = "",
    val email: String = "",
    val external_urls: Map<String, String> = emptyMap(),
    val followers: Followers = Followers(),
    val href: String = "",
    val images: List<SpotifyImage> = emptyList(),
    val product: String = "", // "premium" or "free"
    val type: String = "",
    val uri: String = ""
)

data class Followers(
    val href: String? = null,
    val total: Int = 0
)

data class SpotifyImage(
    val height: Int? = null,
    val url: String = "",
    val width: Int? = null
)
