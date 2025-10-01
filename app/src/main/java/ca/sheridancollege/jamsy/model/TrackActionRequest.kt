package ca.sheridancollege.jamsy.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackActionRequest(
    @Json(name = "isrc")
    val isrc: String?,

    @Json(name = "songName")
    val songName: String,

    @Json(name = "artist")
    val artist: String,

    @Json(name = "genres")
    val genres: String?,

    @Json(name = "action")
    val action: String
)