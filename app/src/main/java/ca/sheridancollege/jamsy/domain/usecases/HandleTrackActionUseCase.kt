package ca.sheridancollege.jamsy.domain.usecases

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.domain.repository.TrackRepository
import ca.sheridancollege.jamsy.util.Resource
import javax.inject.Inject

/**
 * Use case for handling track actions (like/dislike).
 * Contains the business logic for processing user interactions with tracks.
 */
class HandleTrackActionUseCase @Inject constructor(
    private val trackRepository: TrackRepository
) {
    
    /**
     * Execute the use case to like a track.
     * @param track The track to like
     * @param authToken The authentication token
     * @return Resource indicating success or failure
     */
    suspend fun likeTrack(track: Track, authToken: String): Resource<Unit> {
        return trackRepository.likeTrack(track, authToken)
    }
    
    /**
     * Execute the use case to unlike a track.
     * @param track The track to unlike
     * @param authToken The authentication token
     * @return Resource indicating success or failure
     */
    suspend fun unlikeTrack(track: Track, authToken: String): Resource<Unit> {
        return trackRepository.unlikeTrack(track, authToken)
    }
    
    /**
     * Execute the use case to handle a track action.
     * @param track The track to perform action on
     * @param action The action to perform ("like" or "dislike")
     * @param authToken The authentication token
     * @return Resource indicating success or failure
     */
    suspend operator fun invoke(
        track: Track, 
        action: String, 
        authToken: String
    ): Resource<Unit> {
        return when (action.lowercase()) {
            "like" -> likeTrack(track, authToken)
            "dislike" -> unlikeTrack(track, authToken)
            else -> Resource.Error("Invalid action: $action")
        }
    }
}
