package ca.sheridancollege.jamsy.domain.repository

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.util.Resource

/**
 * Repository interface for track operations.
 * Defines the contract for track-related data operations.
 */
interface TrackRepository {
    
    /**
     * Get all tracks.
     * @return Resource containing a list of tracks or error message
     */
    suspend fun getTracks(): Resource<List<Track>>
    
    /**
     * Like a track.
     * @param track The track to like
     * @param authToken The authentication token
     * @return Resource indicating success or failure
     */
    suspend fun likeTrack(track: Track, authToken: String): Resource<Unit>
    
    /**
     * Unlike a track.
     * @param track The track to unlike
     * @param authToken The authentication token
     * @return Resource indicating success or failure
     */
    suspend fun unlikeTrack(track: Track, authToken: String): Resource<Unit>
    
    /**
     * Get liked tracks for the current user.
     * @param authToken The authentication token
     * @return Resource containing a list of liked tracks or error message
     */
    suspend fun getLikedTracks(authToken: String): Resource<List<Track>>
}
