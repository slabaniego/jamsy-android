package ca.sheridancollege.jamsy.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.model.TrackActionRequest
import ca.sheridancollege.jamsy.model.SongAction
import ca.sheridancollege.jamsy.util.Resource

class TrackRepository(private val jamsyRepository: JamsyRepository) {
    // Note: This class requires authentication token for track actions
    // The authToken should be passed from the ViewModel layer

    /**
     * Retrieves a list of tracks from the Jamsy API.
     *
     * @return Resource containing a list of Track objects or error message
     */
    suspend fun getTracks(): Resource<List<Track>> {
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
    suspend fun likeTrack(track: Track, authToken: String): Resource<Boolean> {
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
                    Resource.Success(true)
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
    suspend fun unlikeTrack(track: Track, authToken: String): Resource<Boolean> {
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
                    Resource.Success(true)
                } else {
                    Resource.Error(result.exceptionOrNull()?.message ?: "Failed to unlike track")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to unlike track: ${e.message}")
            }
        }
    }
}