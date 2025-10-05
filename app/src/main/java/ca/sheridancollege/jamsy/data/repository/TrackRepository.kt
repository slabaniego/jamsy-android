package ca.sheridancollege.jamsy.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import ca.sheridancollege.jamsy.domain.models.SongAction
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.domain.repository.TrackRepository as TrackRepositoryInterface
import ca.sheridancollege.jamsy.util.Resource

class TrackRepository(private val jamsyRepository: JamsyRepository) : TrackRepositoryInterface {
    // Note: This class requires authentication token for track actions
    // The authToken should be passed from the ViewModel layer

    /**
     * Retrieves a list of tracks from the Jamsy API.
     *
     * @return Resource containing a list of Track objects or error message
     */
    override suspend fun getTracks(): Resource<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val result = jamsyRepository.getTracks()
                if (result.isSuccess) {
                    Resource.Success(result.getOrNull() ?: emptyList())
                } else {
                    Resource.Error(result.exceptionOrNull()?.message ?: "Failed to load tracks")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to load tracks: ${e.message}")
            }
        }
    }

    /**
     * Likes a track by sending a track action to the API.
     *
     * @param track The track to like
     * @param authToken The authentication token for the API request
     * @return Resource indicating success or failure of the like operation
     */
    override suspend fun likeTrack(track: Track, authToken: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val songAction = SongAction(
                    isrc = track.isrc ?: "",
                    songName = track.name,
                    artist = track.artists.firstOrNull() ?: "",
                    action = "like",
                    genres = track.genres ?: emptyList()
                )
                val result = jamsyRepository.handleTrackAction(songAction, authToken)
                if (result.isSuccess) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(result.exceptionOrNull()?.message ?: "Failed to like track")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to like track: ${e.message}")
            }
        }
    }

    /**
     * Unlikes a track by sending a track action to the API.
     *
     * @param track The track to unlike
     * @param authToken The authentication token for the API request
     * @return Resource indicating success or failure of the unlike operation
     */
    override suspend fun unlikeTrack(track: Track, authToken: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val songAction = SongAction(
                    isrc = track.isrc ?: "",
                    songName = track.name,
                    artist = track.artists.firstOrNull() ?: "",
                    action = "dislike",
                    genres = track.genres ?: emptyList()
                )
                val result = jamsyRepository.handleTrackAction(songAction, authToken)
                if (result.isSuccess) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(result.exceptionOrNull()?.message ?: "Failed to unlike track")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to unlike track: ${e.message}")
            }
        }
    }

    /**
     * Get liked tracks for the current user.
     * @param authToken The authentication token
     * @return Resource containing a list of liked tracks or error message
     */
    override suspend fun getLikedTracks(authToken: String): Resource<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                // This would typically call an API to get liked tracks
                // For now, return empty list as placeholder
                Resource.Success(emptyList())
            } catch (e: Exception) {
                Resource.Error("Failed to get liked tracks: ${e.message}")
            }
        }
    }
}