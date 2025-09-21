package ca.sheridancollege.jamsy.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Artist(
    @Json(name = "id")
    @SerializedName("id")
    val id: String,

    @Json(name = "name")
    @SerializedName("name")
    val name: String,

    @Json(name = "imageUrl")
    @SerializedName("imageUrl")
    val imageUrl: String?,

    @Json(name = "genres")
    @SerializedName("genres")
    val genres: List<String>,

    @Json(name = "popularity")
    @SerializedName("popularity")
    val popularity: Int
)