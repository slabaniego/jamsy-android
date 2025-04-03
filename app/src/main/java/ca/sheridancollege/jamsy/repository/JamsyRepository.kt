package ca.sheridancollege.jamsy.repository
import ca.sheridancollege.jamsy.api.ApiClient
import ca.sheridancollege.jamsy.api.JamsyApiService
import ca.sheridancollege.jamsy.model.SpotifyAuthResponse
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.model.TrackActionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JamsyRepository {
    private val apiService: JamsyApiService = ApiClient.jamsyApiService

    /**
     * Exchange authorization code for access tokens
     */
    suspend fun exchangeCodeForToken(code: String, redirectUri: String): Result<SpotifyAuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.exchangeCodeForToken(code, redirectUri)
                if (response.isSuccessful && response.body() != null) {
                    val networkResponse = response.body()!!
                    // Convert network response to model response
                    val modelResponse = SpotifyAuthResponse(
                        accessToken = networkResponse.accessToken,
                        tokenType = networkResponse.tokenType,
                        firebaseToken = networkResponse.firebaseCustomToken,
                        refreshToken = null,
                        expiresIn = null
                    )
                    Result.success(modelResponse)
                } else {
                    Result.failure(Exception("Failed to exchange code: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Refresh access token
     */
    suspend fun refreshToken(refreshToken: String): Result<Map<String, String>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.refreshToken(refreshToken)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to refresh token: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get recommended tracks with filtering options
     */
    suspend fun getTracks(
        excludeExplicit: Boolean = true,
        excludeLoveSongs: Boolean = false,
        excludeFolk: Boolean = false
    ): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTracks(excludeExplicit, excludeLoveSongs, excludeFolk)
                if (response.isSuccessful && response.body() != null) {
                    val tracksMap = response.body()!!
                    val tracks = tracksMap["tracks"] ?: emptyList()
                    Result.success(tracks)
                } else {
                    Result.failure(Exception("Failed to get tracks: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Record a track action (like/unlike)
     */
    suspend fun trackAction(request: TrackActionRequest): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.trackAction(request)
                if (response.isSuccessful) {
                    val message = response.body()?.get("message") ?: "Success"
                    Result.success(message)
                } else {
                    Result.failure(Exception("Failed to record action: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Search for tracks
     */
    suspend fun searchTracks(
        query: String,
        authToken: String,
        excludeExplicit: Boolean = true,
        excludeLoveSongs: Boolean = false,
        excludeFolk: Boolean = false
    ): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.searchTracks(
                    query,
                    excludeExplicit,
                    excludeLoveSongs,
                    excludeFolk,
                    authHeader
                )

                if (response.isSuccessful && response.body() != null) {
                    val tracksMap = response.body()!!
                    val tracks = tracksMap["tracks"] ?: emptyList()
                    Result.success(tracks)
                } else {
                    Result.failure(Exception("Failed to search tracks: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get currently playing track
     */
    suspend fun getCurrentTrack(authToken: String): Result<Track?> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.getCurrentTrack(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val responseMap = response.body()!!

                    if (responseMap.containsKey("track")) {
                        @Suppress("UNCHECKED_CAST")
                        val trackMap = responseMap["track"] as Map<String, Any>
                        // Would need to convert map to Track object here
                        // Simplified for this example
                        Result.success(null)
                    } else {
                        // No track playing
                        Result.success(null)
                    }
                } else {
                    Result.failure(Exception("Failed to get current track: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get similar tracks
     */
    suspend fun getSimilarTracks(
        trackName: String,
        artistName: List<String>,
        excludeExplicit: Boolean = true,
        excludeLoveSongs: Boolean = false,
        excludeFolk: Boolean = false
    ): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSimilarTracks(
                    trackName,
                    artistName.joinToString(","),
                    excludeExplicit,
                    excludeLoveSongs,
                    excludeFolk
                )

                if (response.isSuccessful && response.body() != null) {
                    val tracksMap = response.body()!!
                    val tracks = tracksMap["tracks"] ?: emptyList()
                    Result.success(tracks)
                } else {
                    Result.failure(Exception("Failed to get similar tracks: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}