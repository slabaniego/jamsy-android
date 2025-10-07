package ca.sheridancollege.jamsy.data.repository

import android.util.Log
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Repository for artist-related operations.
 * Handles artist discovery, caching, and workout-based recommendations.
 */
class ArtistRepositoryImpl {
    
    private val apiService: JamsyApiService = ApiClient.jamsyApiService
    private val cacheManager = ArtistCacheManager()
    
    companion object {
        private const val TAG = "ArtistRepository"
    }
    
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
                Log.d(TAG, "Cache check - valid: ${cacheManager.isCacheValid()}, cached artists: ${cachedArtists?.size ?: 0}")
                
                if (cachedArtists != null && cachedArtists.isNotEmpty()) {
                    Log.d(TAG, "✅ Using cached data for $workout (${cachedArtists.size} artists)")
                    val shuffledArtists = cachedArtists.shuffled().take(WorkoutConstants.Artist.SHUFFLED_RESULT_COUNT)
                    return@withContext Result.success(shuffledArtists)
                }
                
                // Cache miss - fetch from API
                Log.d(TAG, "🔄 Cache miss - fetching artists for $workout from API")
                val result = fetchArtistsFromApi(workout, mood, authToken)
                
                if (result.isSuccess) {
                    val artists = result.getOrNull() ?: emptyList()
                    // Cache the results for future requests
                    cacheManager.put(workout, artists)
                    Log.d(TAG, "✅ Fetched and cached ${artists.size} artists for $workout")
                    return@withContext Result.success(artists.shuffled().take(WorkoutConstants.Artist.SHUFFLED_RESULT_COUNT))
                } else {
                    Log.e(TAG, "❌ Failed to fetch artists: ${result.exceptionOrNull()?.message}")
                    return@withContext result
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Pre-load artists for all workout categories.
     * Mirrors the web flow where artists are loaded during login.
     * 
     * @param authToken The authentication token
     * @return Result indicating success or failure
     */
    suspend fun preloadArtistsForAllWorkouts(authToken: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Pre-loading artists for all workout categories")
                
                for (workout in WorkoutConstants.WORKOUT_TYPES) {
                    val mood = WorkoutConstants.getMoodForWorkout(workout)
                    
                    try {
                        val result = fetchArtistsFromApi(workout, mood, authToken)
                        if (result.isSuccess) {
                            val artists = result.getOrNull() ?: emptyList()
                            cacheManager.put(workout, artists)
                            Log.d(TAG, "Pre-loaded ${artists.size} artists for $workout")
                        } else {
                            Log.e(TAG, "Failed to pre-load artists for $workout: ${result.exceptionOrNull()?.message}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error pre-loading artists for $workout: ${e.message}")
                    }
                    
                    // Delay between requests to avoid rate limiting
                    delay(WorkoutConstants.Cache.PRELOAD_REQUEST_DELAY_MS)
                }
                
                Log.d(TAG, "Pre-loading completed. Cached artists for: ${cacheManager.getCachedWorkouts()}")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Error in pre-loading: ${e.message}")
                Result.failure(e)
            }
        }
    }
    
    /**
     * Populate artist cache during login.
     * Similar to web version's session storage.
     * 
     * @param authToken The authentication token
     * @return Result indicating success or failure
     */
    suspend fun populateArtistCache(authToken: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                for (workout in WorkoutConstants.WORKOUT_TYPES) {
                    val mood = WorkoutConstants.getMoodForWorkout(workout)
                    
                    try {
                        val result = fetchArtistsFromApi(workout, mood, authToken)
                        if (result.isSuccess) {
                            val artists = result.getOrNull() ?: emptyList()
                            cacheManager.put(workout, artists)
                        }
                        
                        // Add delay between requests to avoid rate limiting
                        delay(WorkoutConstants.Cache.API_REQUEST_DELAY_MS)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to cache artists for $workout: ${e.message}")
                    }
                }
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Submit artist selection and get discovery tracks.
     * 
     * @param selectedArtistIds List of selected artist IDs
     * @param artistNamesJson Comma-separated artist names
     * @param workout The workout type
     * @param mood The mood type
     * @param action The action type
     * @param authToken The authentication token
     * @return Result containing list of tracks or failure
     */
    suspend fun submitArtistSelection(
        selectedArtistIds: List<String>,
        artistNamesJson: String,
        workout: String,
        mood: String,
        action: String,
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
    
    /**
     * Clear the artist cache.
     */
    fun clearCache() {
        cacheManager.clear()
    }
}

