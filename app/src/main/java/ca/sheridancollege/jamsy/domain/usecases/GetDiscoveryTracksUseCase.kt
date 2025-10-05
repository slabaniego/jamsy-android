package ca.sheridancollege.jamsy.domain.usecases

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.domain.repository.TrackRepository
import ca.sheridancollege.jamsy.util.Resource
import javax.inject.Inject

/**
 * Use case for getting discovery tracks.
 * Contains the business logic for retrieving tracks for music discovery.
 */
class GetDiscoveryTracksUseCase @Inject constructor(
    private val trackRepository: TrackRepository
) {
    
    /**
     * Execute the use case to get discovery tracks.
     * @param authToken The authentication token
     * @return Resource containing a list of discovery tracks or error message
     */
    suspend operator fun invoke(authToken: String): Resource<List<Track>> {
        return trackRepository.getTracks()
    }
    
    /**
     * Get discovery tracks without authentication.
     * @return Resource containing a list of discovery tracks or error message
     */
    suspend fun getBasicDiscoveryTracks(): Resource<List<Track>> {
        return trackRepository.getTracks()
    }
}
