package ca.sheridancollege.jamsy.data.repository

import ca.sheridancollege.jamsy.data.datasource.remote.ApiClient
import ca.sheridancollege.jamsy.data.datasource.remote.JamsyApiService
import ca.sheridancollege.jamsy.data.datasource.remote.SpotifyAuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for Spotify authentication operations.
 * Handles token exchange and refresh operations with the backend.
 */
class SpotifyAuthRepositoryImpl {
    
    private val apiService: JamsyApiService = ApiClient.jamsyApiService
    
    /**
     * Exchange authorization code for access tokens.
     * 
     * @param code The authorization code from Spotify
     * @param redirectUri The redirect URI used in the authorization flow
     * @return Result containing the SpotifyAuthResponse or failure
     */
    suspend fun exchangeCodeForToken(code: String, redirectUri: String): Result<SpotifyAuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.exchangeCodeForToken(code, redirectUri)
                if (response.isSuccessful && response.body() != null) {
                    val wrapper = response.body()!!
                    // Extract the nested SpotifyAuthResponse from the wrapper
                    Result.success(wrapper.spotify)
                } else {
                    Result.failure(Exception("Failed to exchange code: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Refresh access token using refresh token.
     * 
     * @param refreshToken The refresh token
     * @return Result containing the new SpotifyAuthResponse or failure
     */
    @Suppress("unused")
    suspend fun refreshToken(refreshToken: String): Result<SpotifyAuthResponse> {
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

