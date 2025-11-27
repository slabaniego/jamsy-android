/*
 * PlaylistRepositoryImpl.kt
 * Repository for playlist preview generation and Spotify playlist creation.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.data.repository

import ca.sheridancollege.jamsy.data.datasource.remote.ApiClient
import ca.sheridancollege.jamsy.data.datasource.remote.CreatePlaylistRequest
import ca.sheridancollege.jamsy.data.datasource.remote.JamsyApiService
import ca.sheridancollege.jamsy.data.mappers.TrackMapper
import ca.sheridancollege.jamsy.domain.models.PreviewPlaylistRequest
import ca.sheridancollege.jamsy.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for playlist-related operations.
 * Handles playlist creation, templates, recommendations, and previews.
 */
class PlaylistRepositoryImpl {
    
    private val apiService: JamsyApiService = ApiClient.jamsyApiService
    
    /**
     * Get preview playlist.
     * 
     * @param authToken The authentication token
     * @param likedTracks List of liked tracks
     * @return Result containing list of tracks for preview or failure
     */
    suspend fun getPreviewPlaylist(authToken: String, likedTracks: List<Track>): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val requestBody = PreviewPlaylistRequest(likedTracks)
                val response = apiService.getPreviewPlaylist(authHeader, requestBody)
                
                if (response.isSuccessful && response.body() != null) {
                    val tracksDto = response.body()!!
                    // Use type-safe mapper to convert DTOs to domain models
                    val tracks = TrackMapper.toDomainModelList(tracksDto.tracks)
                    Result.success(tracks)
                } else {
                    Result.failure(Exception("Failed to get preview playlist: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Creates a new playlist on Spotify using the provided tracks.
     *
     * This function sends a request to the Jamsy API which then communicates with Spotify's API
     * to create a playlist in the user's Spotify account with the specified tracks.
     *
     * @param authToken The Spotify access token for API authentication (used in Bearer header)
     * @param tracks List of tracks to include in the new playlist
     * @return Result containing the Spotify playlist URL on success, or an exception on failure
     */
    suspend fun createPlaylist(authToken: String, tracks: List<Track>): Result<String> {
        // Execute the network operation on the IO dispatcher to avoid blocking the main thread
        return withContext(Dispatchers.IO) {
            try {
                // Prepare the authorization header for the API request
                val authHeader = "Bearer $authToken"

                // Create the request body with the list of tracks to be added to the playlist
                val requestBody = CreatePlaylistRequest(tracks = tracks)

                // Make the API call to create the playlist through our backend service
                val response = apiService.createPlaylist(authHeader, requestBody)

                // Check if the HTTP response was successful and contains a response body
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!

                    // Extract the status and playlist URL from the response JSON
                    val status = responseBody["status"]
                    val playlistUrl = responseBody["playlistUrl"] ?: ""

                    // Validate that the operation was successful and we received a valid URL
                    if (status == "success" && playlistUrl.isNotEmpty()) {
                        // Return success with the playlist URL for the caller to use
                        Result.success(playlistUrl)
                    } else {
                        // Handle API-level failures (e.g., Spotify API errors)
                        val message = responseBody["message"] ?: "Failed to create playlist"
                        Result.failure(Exception(message))
                    }
                } else {
                    // Handle HTTP-level failures (e.g., network errors, server errors)
                    Result.failure(Exception("Failed to create playlist: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                // Catch any unexpected exceptions during the operation
                Result.failure(e)
            }
        }
    }
}

