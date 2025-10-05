package ca.sheridancollege.jamsy.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Artist(
    @Json(name = "id")
    val id: String? = null,

    @Json(name = "name")
    val name: String,

    @Json(name = "imageUrl")
    val imageUrl: String?,

    @Json(name = "genres")
    val genres: List<String>? = null,

    @Json(name = "popularity")
    val popularity: Int? = null,

    // For compatibility with the UI that expects images list
    @Json(name = "images")
    val images: List<String>? = null
) {
    // Helper property to get the first image URL
    val firstImageUrl: String?
        get() = imageUrl ?: images?.firstOrNull()
}