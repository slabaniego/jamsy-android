package ca.sheridancollege.jamsy.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SongAction(
    @Json(name = "id")
    @SerializedName("id")
    val id: Long? = null,

    @Json(name = "isrc")
    @SerializedName("isrc")
    val isrc: String,

    @Json(name = "songName")
    @SerializedName("songName")
    val songName: String,

    @Json(name = "artist")
    @SerializedName("artist")
    val artist: String,

    @Json(name = "action")
    @SerializedName("action")
    val action: String, // "like" or "dislike"

    @Json(name = "genres")
    @SerializedName("genres")
    val genres: List<String>? = null
)