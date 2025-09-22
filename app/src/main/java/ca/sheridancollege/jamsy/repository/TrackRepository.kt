package ca.sheridancollege.jamsy.repository
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.model.TrackActionRequest
import ca.sheridancollege.jamsy.model.SongAction
import ca.sheridancollege.jamsy.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepository(private val jamsyRepository: JamsyRepository) {
    
    // Note: This class requires authentication token for track actions
    // The authToken should be passed from the ViewModel layer

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

    suspend fun likeTrack(track: Track, authToken: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val songAction = SongAction(
                    isrc = track.isrc ?: "",
                    songName = track.name,
                    artist = track.artists.firstOrNull() ?: "",
                    action = "like",
                    genres = track.genres
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

    suspend fun unlikeTrack(track: Track, authToken: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val songAction = SongAction(
                    isrc = track.isrc ?: "",
                    songName = track.name,
                    artist = track.artists.firstOrNull() ?: "",
                    action = "dislike",
                    genres = track.genres
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