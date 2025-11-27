/*
 * ArtistDto.kt
 * DTOs for artist data and artist list responses from the Jamsy backend.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistDto(
    @Json(name = "id")
    val id: String?,
    
    @Json(name = "name")
    val name: String,
    
    @Json(name = "imageUrl")
    val imageUrl: String?,
    
    @Json(name = "genres")
    val genres: List<String>?,
    
    @Json(name = "popularity")
    val popularity: Int?,
    
    @Json(name = "images")
    val images: List<String>?,
    
    @Json(name = "workoutCategories")
    val workoutCategories: List<String>?
)

/**
 * Wrapper for artists list response.
 */
@JsonClass(generateAdapter = true)
data class ArtistsResponseDto(
    @Json(name = "artists")
    val artists: List<ArtistDto>
)

