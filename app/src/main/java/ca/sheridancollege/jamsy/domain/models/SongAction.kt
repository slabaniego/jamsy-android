package ca.sheridancollege.jamsy.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SongAction(
    @Json(name = "id")
    val id: Long? = null,

    @Json(name = "isrc")
    val isrc: String,

    @Json(name = "songName")
    val songName: String,

    @Json(name = "artist")
    val artist: String,

    @Json(name = "action")
    val action: String, // "like" or "dislike"

    @Json(name = "genres")
    val genres: List<String>? = null
)