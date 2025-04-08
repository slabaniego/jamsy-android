package ca.sheridancollege.jamsy.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.google.gson.annotations.SerializedName

@JsonClass(generateAdapter = true)
data class Track(
    @Json(name = "id")
    @SerializedName("id")
    val id: String? = null,

    @Json(name = "isrc")
    @SerializedName("isrc")
    val isrc: String? = null,

    @Json(name = "name")
    @SerializedName("name")
    val name: String,

    @Json(name = "artists")
    @SerializedName("artists")
    val artists: List<String>,

    @Json(name = "albumName")
    @SerializedName("albumName")
    val albumName: String? = null,

    @Json(name = "albumCover")
    @SerializedName("albumCover")
    val albumCover: String? = null,

    @Json(name = "previewUrl")
    @SerializedName("previewUrl")
    val previewUrl: String? = null,

    @Json(name = "explicit")
    @SerializedName("explicit")
    val explicit: Boolean = false,

    @Json(name = "genres")
    @SerializedName("genres")
    val genres: List<String>? = null,

    @Json(name = "popularity")
    @SerializedName("popularity")
    val popularity: Int? = null
)