package ca.sheridancollege.jamsy.data.mappers

import ca.sheridancollege.jamsy.data.datasource.remote.dto.TrackDto
import ca.sheridancollege.jamsy.domain.models.Track

/**
 * Mapper utility for converting Track DTOs to domain models.
 * Type-safe conversion from data layer to domain layer.
 */
object TrackMapper {
    
    /**
     * Converts a TrackDto to a Track domain model.
     * 
     * @param dto The track DTO from API response
     * @return Track domain model
     */
    fun toDomainModel(dto: TrackDto): Track {
        return Track(
            id = dto.id,
            name = dto.name ?: "",
            artists = dto.artists ?: emptyList(),
            albumCover = dto.albumCover,
            imageUrl = dto.imageUrl,
            previewUrl = dto.previewUrl,
            durationMs = dto.durationMs ?: 0,
            isrc = dto.isrc,
            explicit = dto.explicit ?: false,
            genres = dto.genres,
            popularity = dto.popularity ?: 0,
            externalUrl = dto.externalUrl,
            artistName = dto.artistName
        )
    }
    
    /**
     * Converts a list of TrackDto to Track domain models.
     * 
     * @param dtos List of track DTOs from API response
     * @return List of Track domain models
     */
    fun toDomainModelList(dtos: List<TrackDto>): List<Track> {
        return dtos.map { toDomainModel(it) }
    }
}