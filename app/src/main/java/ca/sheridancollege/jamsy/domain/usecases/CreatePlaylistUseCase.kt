package ca.sheridancollege.jamsy.domain.usecases

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.domain.repository.TrackRepository
import ca.sheridancollege.jamsy.util.Resource
import javax.inject.Inject

/**
 * Use case for creating playlists.
 * Contains the business logic for playlist creation operations.
 */
class CreatePlaylistUseCase @Inject constructor(
    private val trackRepository: TrackRepository
) {
    
    /**
     * Execute the use case to create a playlist from liked tracks.
     * @param authToken The authentication token
     * @return Resource containing playlist creation result or error message
     */
    suspend operator fun invoke(authToken: String): Resource<Map<String, String>> {
        // First get the user's liked tracks
        val likedTracksResult = trackRepository.getLikedTracks(authToken)
        
        return when (likedTracksResult) {
            is Resource.Success -> {
                val likedTracks = likedTracksResult.data
                if (likedTracks.isNotEmpty()) {
                    // Business logic: Create playlist with liked tracks
                    // This would typically call a playlist service
                    Resource.Success(mapOf(
                        "playlistId" to "generated_playlist_id",
                        "playlistUrl" to "https://open.spotify.com/playlist/generated",
                        "trackCount" to likedTracks.size.toString()
                    ))
                } else {
                    Resource.Error("No liked tracks available to create playlist")
                }
            }
            is Resource.Error -> Resource.Error(likedTracksResult.message)
            is Resource.Loading -> Resource.Loading
        }
    }
    
    /**
     * Preview a playlist before creation.
     * @param authToken The authentication token
     * @return Resource containing preview tracks or error message
     */
    suspend fun previewPlaylist(authToken: String): Resource<List<Track>> {
        return trackRepository.getLikedTracks(authToken)
    }
}
