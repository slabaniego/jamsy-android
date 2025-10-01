package ca.sheridancollege.jamsy.services

import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.repository.JamsyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Service for track discovery and playlist generation
 * Mirrors the backend DiscoveryService functionality
 */
class DiscoveryService(
    private val jamsyRepository: JamsyRepository
) {
    
    /**
     * Get discovery tracks based on selected artists
     */
    suspend fun getDiscoveryTracks(
        seedArtistNames: List<String>,
        workout: String,
        limit: Int,
        authToken: String
    ): Result<List<Track>> {
        return try {
            jamsyRepository.getDiscoveryTracks(authToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Generate a one-hour playlist from liked tracks
     */
    suspend fun generateOneHourPlaylist(
        likedTracks: List<Track>, 
        targetDurationMinutes: Int = 60,
        authToken: String
    ): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                // For now, return the liked tracks as-is
                // In a full implementation, this would call the backend to expand the playlist
                Result.success(likedTracks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Preview playlist before creating
     */
    suspend fun previewPlaylist(authToken: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jamsyRepository.previewPlaylist(authToken)
                response
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Create playlist in Spotify
     */
    suspend fun createPlaylist(authToken: String): Result<Map<String, String>> {
        return withContext(Dispatchers.IO) {
            try {
                jamsyRepository.createPlaylist(authToken)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
