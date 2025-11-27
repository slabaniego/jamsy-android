/*
 * SpotifyUserResponse.kt
 * Models Spotify user profile API response for the /v1/me endpoint.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.data.datasource.remote

data class SpotifyUserResponse(
    val id: String = "",
    val displayName: String = "",
    val email: String = "",
    val externalUrls: Map<String, String> = emptyMap(),
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
