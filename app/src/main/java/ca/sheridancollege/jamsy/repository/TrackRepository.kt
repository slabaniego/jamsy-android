package ca.sheridancollege.jamsy.repository
import ca.sheridancollege.jamsy.api.TrackApiClient
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.util.Resource

class TrackRepository(private val apiClient: TrackApiClient) {

    suspend fun getTracks(): Resource<List<Track>> {
        return apiClient.getTracks()
    }

    suspend fun likeTrack(track: Track): Resource<Boolean> {
        return apiClient.likeTrack(track)
    }

    suspend fun unlikeTrack(track: Track): Resource<Boolean> {
        return apiClient.unlikeTrack(track)
    }
}