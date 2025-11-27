package ca.sheridancollege.jamsy.data.repository

import ca.sheridancollege.jamsy.data.cache.ArtistCacheManager
import ca.sheridancollege.jamsy.data.datasource.remote.ApiClient
import ca.sheridancollege.jamsy.data.datasource.remote.DiscoveryRequest
import ca.sheridancollege.jamsy.data.datasource.remote.JamsyApiService
import ca.sheridancollege.jamsy.data.mappers.ArtistMapper
import ca.sheridancollege.jamsy.data.mappers.TrackMapper
import ca.sheridancollege.jamsy.domain.constants.WorkoutConstants
import ca.sheridancollege.jamsy.domain.models.Artist
import ca.sheridancollege.jamsy.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for artist-related operations.
 * Handles artist discovery, caching, and workout-based recommendations.
 */
class ArtistRepositoryImpl {
    
    private val apiService: JamsyApiService = ApiClient.jamsyApiService
    private val cacheManager = ArtistCacheManager()
    
    /**
     * Get artists by workout and mood with smart caching.
     * Fetches on-demand if cache is empty or expired.
     * 
     * @param workout The workout type
     * @param mood The mood type
     * @param authToken The authentication token
     * @return Result containing list of artists or failure
     */
    suspend fun getArtistsByWorkout(
        workout: String,
        mood: String,
        authToken: String
    ): Result<List<Artist>> {
        return withContext(Dispatchers.IO) {
            try {
                // Check cache first
                val cachedArtists = cacheManager.get(workout)
                
                if (cachedArtists != null && cachedArtists.isNotEmpty()) {
                    val shuffledArtists = cachedArtists.shuffled()
                        .take(WorkoutConstants.Artist.SHUFFLED_RESULT_COUNT)
                    return@withContext Result.success(shuffledArtists)
                }
                
                // Cache miss - fetch from API
                val result = fetchArtistsFromApi(workout, mood, authToken)
                
                if (result.isSuccess) {
                    val artists = result.getOrNull() ?: emptyList()
                    // Cache the results for future requests
                    cacheManager.put(workout, artists)
                    val shuffledArtists = artists.shuffled()
                        .take(WorkoutConstants.Artist.SHUFFLED_RESULT_COUNT)
                    return@withContext Result.success(shuffledArtists)
                } else {
                    return@withContext result
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Submit artist selection and get discovery tracks.
     * 
     * @param artistNamesJson Comma-separated artist names
     * @param workout The workout type
     * @param authToken The authentication token
     * @return Result containing list of tracks or failure
     */
    suspend fun submitArtistSelection(
        artistNamesJson: String,
        workout: String,
        authToken: String
    ): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                
                // Parse artist names from JSON string
                val artistNames = artistNamesJson.split(",").map { it.trim() }
                
                // Create request body for mobile API
                val requestBody = DiscoveryRequest(
                    seedArtists = artistNames,
                    workout = workout
                )
                
                val response = apiService.submitArtistSelection(
                    requestBody = requestBody,
                    authHeader = authHeader
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val tracksDto = response.body()!!
                    // Use type-safe mapper to convert DTOs to domain models
                    val tracks = TrackMapper.toDomainModelList(tracksDto.tracks)
                    Result.success(tracks)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to submit selection: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Fetch artists from API (bypasses cache).
     * 
     * @param workout The workout type
     * @param mood The mood type
     * @param authToken The authentication token
     * @return Result containing list of artists or failure
     */
    private suspend fun fetchArtistsFromApi(
        workout: String,
        mood: String,
        authToken: String
    ): Result<List<Artist>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.getArtistsByWorkout(workout, mood, authHeader)
                
                if (response.isSuccessful && response.body() != null) {
                    val artistsDto = response.body()!!
                    // Use type-safe mapper to convert DTOs to domain models
                    val artists = ArtistMapper.toDomainModelList(artistsDto.artists)
                    Result.success(artists)
                } else {
                    Result.failure(Exception("Failed to get artists: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

