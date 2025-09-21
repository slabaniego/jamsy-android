package ca.sheridancollege.jamsy.repository
import ca.sheridancollege.jamsy.api.ApiClient
import ca.sheridancollege.jamsy.network.JamsyApiService
import ca.sheridancollege.jamsy.model.*
import ca.sheridancollege.jamsy.network.SpotifyAuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JamsyRepository {
    private val apiService: JamsyApiService = ApiClient.jamsyApiService

    /**
     * Exchange authorization code for access tokens - matches /api/auth/token
     */
    suspend fun exchangeCodeForToken(code: String, redirectUri: String): Result<SpotifyAuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.exchangeCodeForToken(code, redirectUri)
                if (response.isSuccessful && response.body() != null) {
                    val networkResponse = response.body()!!
                    val modelResponse = SpotifyAuthResponse(
                        accessToken = networkResponse.accessToken,
                        tokenType = networkResponse.tokenType,
                        firebaseCustomToken = networkResponse.firebaseCustomToken
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
     * Search for tracks - matches /api/search
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
                    query, excludeExplicit, excludeLoveSongs, excludeFolk, authHeader
                )

                if (response.isSuccessful && response.body() != null) {
                    val tracksMap = response.body()!!
                    @Suppress("UNCHECKED_CAST")
                    val tracks = tracksMap["tracks"] as? List<Track> ?: emptyList()
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
     * Record track action - matches /api/track/action
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
     * Refresh access token - matches /api/auth/refresh
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
}