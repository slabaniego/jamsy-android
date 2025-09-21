package ca.sheridancollege.jamsy.repository
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.model.TrackActionRequest
import ca.sheridancollege.jamsy.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepository(private val jamsyRepository: JamsyRepository) {

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

    suspend fun likeTrack(track: Track): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val request = TrackActionRequest(
                    isrc = track.isrc,
                    songName = track.name,
                    artist = track.artists,
                    genres = track.genres?.joinToString(","),
                    action = "like"
                )
                val result = jamsyRepository.trackAction(request)
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

    suspend fun unlikeTrack(track: Track): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val request = TrackActionRequest(
                    isrc = track.isrc,
                    songName = track.name,
                    artist = track.artists,
                    genres = track.genres?.joinToString(","),
                    action = "unlike"
                )
                val result = jamsyRepository.trackAction(request)
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