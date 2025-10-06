package ca.sheridancollege.jamsy.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaylistTemplate(
    @Json(name = "name")
    val name: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "seedGenres")
    val seedGenres: List<String>,

    @Json(name = "targetEnergy")
    val targetEnergy: Int,

    @Json(name = "targetTempo")
    val targetTempo: Int,

    @Json(name = "includeExplicit")
    val includeExplicit: Boolean,

    @Json(name = "mood")
    val mood: String
)