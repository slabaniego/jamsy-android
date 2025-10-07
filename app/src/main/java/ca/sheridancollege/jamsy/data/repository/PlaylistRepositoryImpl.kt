package ca.sheridancollege.jamsy.data.repository

import android.util.Log
import ca.sheridancollege.jamsy.data.datasource.remote.ApiClient
import ca.sheridancollege.jamsy.data.datasource.remote.CreatePlaylistRequest
import ca.sheridancollege.jamsy.data.datasource.remote.JamsyApiService
import ca.sheridancollege.jamsy.data.mappers.TrackMapper
import ca.sheridancollege.jamsy.domain.models.PlaylistTemplate
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
    
    companion object {
        private const val TAG = "PlaylistRepository"
    }
    
    /**
     * Get playlist templates.
     * 
     * @param authToken The authentication token
     * @return Result containing list of playlist templates or failure
     */
    suspend fun getPlaylistTemplates(authToken: String): Result<List<PlaylistTemplate>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.getPlaylistTemplates(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to get playlist templates: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get playlist by template.
     * 
     * @param templateName The template name
     * @param accessToken The access token
     * @return Result containing list of tracks or failure
     */
    @Suppress("unused")
    suspend fun getPlaylistByTemplate(templateName: String, accessToken: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPlaylistByTemplate(templateName, accessToken)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to get playlist by template: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get recommendations by template.
     * 
     * @param templateName The template name
     * @param authToken The authentication token
     * @return Result containing list of tracks or failure
     */
    suspend fun getRecommendationsByTemplate(templateName: String, authToken: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.getRecommendations(templateName, authToken, authHeader)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to get recommendations: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
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
                Log.d(TAG, "Sending ${likedTracks.size} liked tracks to preview-playlist endpoint")
                val authHeader = "Bearer $authToken"
                val requestBody = PreviewPlaylistRequest(likedTracks)
                val response = apiService.getPreviewPlaylist(authHeader, requestBody)
                
                if (response.isSuccessful && response.body() != null) {
                    val tracksDto = response.body()!!
                    // Use type-safe mapper to convert DTOs to domain models
                    val tracks = TrackMapper.toDomainModelList(tracksDto.tracks)
                    Log.d(TAG, "Received ${tracks.size} tracks from preview-playlist")
                    Result.success(tracks)
                } else {
                    Result.failure(Exception("Failed to get preview playlist: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting preview playlist: ${e.message}")
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
    
    /**
     * Create playlist in Spotify.
     * 
     * @param authToken The authentication token
     * @param tracks List of tracks to include in playlist
     * @return Result containing playlist URL or failure
     */
    suspend fun createPlaylist(authToken: String, tracks: List<Track>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val requestBody = CreatePlaylistRequest(tracks = tracks)
                val response = apiService.createPlaylist(authHeader, requestBody)
                
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    val status = responseBody["status"]
                    val playlistUrl = responseBody["playlistUrl"] ?: ""
                    
                    if (status == "success" && playlistUrl.isNotEmpty()) {
                        Result.success(playlistUrl)
                    } else {
                        val message = responseBody["message"] ?: "Failed to create playlist"
                        Result.failure(Exception(message))
                    }
                } else {
                    Result.failure(Exception("Failed to create playlist: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

