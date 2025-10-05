package ca.sheridancollege.jamsy.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Track(
    @Json(name = "id")
    val id: String? = null,

    @Json(name = "externalUrl")
    val externalUrl: String? = null,

    @Json(name = "popularity")
    val popularity: Int = 0,

    @Json(name = "name")
    val name: String = "",

    @Json(name = "isrc")
    val isrc: String? = null,

    @Json(name = "explicit")
    val explicit: Boolean = false,

    @Json(name = "previewUrl")
    val previewUrl: String? = null,

    @Json(name = "albumCover")
    val albumCover: String? = null,

    @Json(name = "artists")
    val artists: List<String> = emptyList(),

    @Json(name = "genres")
    val genres: List<String>? = null,

    @Json(name = "artistName")
    val artistName: String? = null,

    @Json(name = "imageUrl")
    val imageUrl: String? = null,

    @Json(name = "durationMs")
    val durationMs: Int = 0
)