package ca.sheridancollege.jamsy.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Track(
    @Json(name = "id")
    val id: String? = null,

    @Json(name = "isrc")
    val isrc: String? = null,

    @Json(name = "name")
    val name: String,

    @Json(name = "artists")
    val artists: List<String>,

    @Json(name = "albumName")
    val albumName: String? = null,

    @Json(name = "albumCover")
    val albumCover: String? = null,

    @Json(name = "previewUrl")
    val previewUrl: String? = null,

    @Json(name = "explicit")
    val explicit: Boolean = false,

    @Json(name = "genres")
    val genres: List<String>? = null,

    @Json(name = "popularity")
    val popularity: Int? = null
)