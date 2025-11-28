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
import ca.sheridancollege.jamsy.data.datasource.remote.dto.CreatePlaylistResponseDto
import ca.sheridancollege.jamsy.data.mappers.TrackMapper
import ca.sheridancollege.jamsy.domain.models.PreviewPlaylistRequest
import ca.sheridancollege.jamsy.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
class PlaylistCreationNetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
class PlaylistCreationServerException(message: String) : Exception(message)
class PlaylistCreationIncompleteException(message: String = "Playlist created but no URL returned") : Exception(message)

class PlaylistRepositoryImpl {
    private val apiService: JamsyApiService = ApiClient.jamsyApiService

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
    suspend fun createPlaylist(authToken: String, tracks: List<Track>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = buildAuthHeader(authToken)
                val request = buildCreatePlaylistRequest(tracks)
                val response = performCreatePlaylistCall(authHeader, request)
                parseCreatePlaylistResponse(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun buildAuthHeader(authToken: String): String = "Bearer $authToken"

    private fun buildCreatePlaylistRequest(tracks: List<Track>): CreatePlaylistRequest =
        CreatePlaylistRequest(tracks = tracks)

    private suspend fun performCreatePlaylistCall(
        authHeader: String,
        request: CreatePlaylistRequest
    ): retrofit2.Response<CreatePlaylistResponseDto> =
        apiService.createPlaylist(authHeader, request)

    private fun parseCreatePlaylistResponse(
        response: retrofit2.Response<CreatePlaylistResponseDto>
    ): Result<String> {
        return when {
            response.isSuccessful && response.body() != null -> {
                parseSuccessfulResponse(response.body()!!)
            }
            else -> {
                val errorMessage = response.errorBody()?.string()
                    ?: "Unknown network error"
                Result.failure(PlaylistCreationNetworkException("Failed to create playlist: $errorMessage"))
            }
        }
    }

    private fun parseSuccessfulResponse(responseBody: CreatePlaylistResponseDto): Result<String> {
        return when (responseBody.status) {
            "success" -> {
                responseBody.playlistUrl?.let { url ->
                    if (url.isNotEmpty()) {
                        Result.success(url)
                    } else {
                        Result.failure(PlaylistCreationIncompleteException())
                    }
                } ?: Result.failure(PlaylistCreationIncompleteException())
            }
            else -> {
                val message = responseBody.message ?: "Failed to create playlist"
                Result.failure(PlaylistCreationServerException(message))
            }
        }
    }
}

