package ca.sheridancollege.jamsy.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaylistTemplate(
    @Json(name = "id")
    @SerializedName("id")
    val id: String,

    @Json(name = "name")
    @SerializedName("name")
    val name: String,

    @Json(name = "description")
    @SerializedName("description")
    val description: String,

    @Json(name = "genres")
    @SerializedName("genres")
    val genres: List<String>,

    @Json(name = "minTempo")
    @SerializedName("minTempo")
    val minTempo: Int,

    @Json(name = "maxTempo")
    @SerializedName("maxTempo")
    val maxTempo: Int,

    @Json(name = "isExplicit")
    @SerializedName("isExplicit")
    val isExplicit: Boolean
)