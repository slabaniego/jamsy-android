package ca.sheridancollege.jamsy.data.mappers

import ca.sheridancollege.jamsy.data.datasource.remote.dto.ArtistDto
import ca.sheridancollege.jamsy.domain.models.Artist

/**
 * Mapper utility for converting Artist DTOs to domain models.
 * Type-safe conversion from data layer to domain layer.
 */
object ArtistMapper {
    
    /**
     * Converts an ArtistDto to an Artist domain model.
     * 
     * @param dto The artist DTO from API response
     * @return Artist domain model
     */
    fun toDomainModel(dto: ArtistDto): Artist {
        return Artist(
            id = dto.id ?: "",
            name = dto.name,
            imageUrl = dto.imageUrl ?: "",
            genres = dto.genres ?: emptyList(),
            popularity = dto.popularity ?: 0,
            images = dto.images,
            workoutCategories = dto.workoutCategories ?: emptyList()
        )
    }
    
    /**
     * Converts a list of ArtistDto to Artist domain models.
     * 
     * @param dtos List of artist DTOs from API response
     * @return List of Artist domain models
     */
    fun toDomainModelList(dtos: List<ArtistDto>): List<Artist> {
        return dtos.map { toDomainModel(it) }
    }
}