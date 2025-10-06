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
            jamsyRepository.getRecommendationsByTemplate(templateName, accessToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
