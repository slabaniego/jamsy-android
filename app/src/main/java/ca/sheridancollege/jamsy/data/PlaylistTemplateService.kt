package ca.sheridancollege.jamsy.data

import ca.sheridancollege.jamsy.data.repository.JamsyRepository
import ca.sheridancollege.jamsy.domain.models.PlaylistTemplate
import ca.sheridancollege.jamsy.domain.models.Track

/**
 * Service for managing playlist templates and generating recommendations
 * Mirrors the backend PlaylistTemplateService functionality
 */
class PlaylistTemplateService(
    private val jamsyRepository: JamsyRepository
) {
    
    /**
     * Get all available playlist templates
     */
    suspend fun getDefaultTemplates(authToken: String): Result<List<PlaylistTemplate>> {
        return try {
            jamsyRepository.getPlaylistTemplates(authToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get recommended tracks based on a specific template
     */
    suspend fun getRecommendationsFromTemplate(
        templateName: String, 
        accessToken: String
    ): Result<List<Track>> {
        return try {
            jamsyRepository.getPlaylistByTemplate(templateName, accessToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get default templates (fallback if API fails)
     */
    fun getDefaultTemplatesLocal(): List<PlaylistTemplate> {
        return listOf(
            PlaylistTemplate(
                id = "yoga",
                name = "Yoga Session",
                description = "Calm and relaxing background music",
                genres = listOf("ambient", "acoustic", "chill"),
                minTempo = 30,
                maxTempo = 80,
                isExplicit = false
            ),
            PlaylistTemplate(
                id = "weightlifting",
                name = "Weight Lifting",
                description = "Push yourself with heavy and motivational beats",
                genres = listOf("rock", "metal", "alternative", "hip-hop"),
                minTempo = 85,
                maxTempo = 130,
                isExplicit = true
            ),
            PlaylistTemplate(
                id = "running",
                name = "Running",
                description = "Pace yourself and keep moving",
                genres = listOf("edm", "pop", "dance", "house"),
                minTempo = 90,
                maxTempo = 160,
                isExplicit = false
            )
        )
    }
}
            
        
    

