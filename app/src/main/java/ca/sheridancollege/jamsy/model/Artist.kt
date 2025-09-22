package ca.sheridancollege.jamsy.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Artist(
    @Json(name = "id")
    @SerializedName("id")
    val id: String? = null,

    @Json(name = "name")
    @SerializedName("name")
    val name: String,

    @Json(name = "imageUrl")
    @SerializedName("imageUrl")
    val imageUrl: String?,

    @Json(name = "genres")
    @SerializedName("genres")
    val genres: List<String>? = null,

    @Json(name = "popularity")
    @SerializedName("popularity")
    val popularity: Int? = null,

    // For compatibility with the UI that expects images list
    @Json(name = "images")
    @SerializedName("images")
    val images: List<String>? = null
) {
    // Helper property to get the first image URL
    val firstImageUrl: String?
        get() = imageUrl ?: images?.firstOrNull()
}