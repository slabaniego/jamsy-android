package ca.sheridancollege.jamsy.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaylistTemplate(
    @Json(name = "id")
    val id: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "genres")
    val genres: List<String>,

    @Json(name = "minTempo")
    val minTempo: Int,

    @Json(name = "maxTempo")
    val maxTempo: Int,

    @Json(name = "isExplicit")
    val isExplicit: Boolean
)