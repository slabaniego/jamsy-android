/*
 * TrackActionRequest.kt
 * Domain DTO representing a track action request sent to the backend.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.domain.models
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