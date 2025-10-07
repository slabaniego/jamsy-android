package ca.sheridancollege.jamsy.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object for Track API responses.
 * Maps directly to the JSON structure returned by the backend.
 */
@JsonClass(generateAdapter = true)
data class TrackDto(
    @Json(name = "id")
    val id: String?,
    
    @Json(name = "name")
    val name: String?,
    
    @Json(name = "artists")
    val artists: List<String>?,
    
    @Json(name = "albumCover")
    val albumCover: String?,
    
    @Json(name = "imageUrl")
    val imageUrl: String?,
    
    @Json(name = "previewUrl")
    val previewUrl: String?,
    
    @Json(name = "durationMs")
    val durationMs: Int?,
    
    @Json(name = "isrc")
    val isrc: String?,
    
    @Json(name = "explicit")
    val explicit: Boolean?,
    
    @Json(name = "genres")
    val genres: List<String>?,
    
    @Json(name = "popularity")
    val popularity: Int?,
    
    @Json(name = "externalUrl")
    val externalUrl: String?,
    
    @Json(name = "artistName")
    val artistName: String?
)

/**
 * Wrapper for tracks list response.
 */
@JsonClass(generateAdapter = true)
data class TracksResponseDto(
    @Json(name = "tracks")
    val tracks: List<TrackDto>
)

