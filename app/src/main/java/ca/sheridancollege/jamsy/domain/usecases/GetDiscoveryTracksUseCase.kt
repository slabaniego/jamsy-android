/*
 * GetDiscoveryTracksUseCase.kt
 * Use case for retrieving discovery tracks from the domain repository.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.domain.usecases

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.domain.repository.TrackRepository
import ca.sheridancollege.jamsy.util.Resource
import javax.inject.Inject
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
