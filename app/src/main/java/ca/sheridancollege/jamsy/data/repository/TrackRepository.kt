package ca.sheridancollege.jamsy.data.repository

import ca.sheridancollege.jamsy.data.datasource.remote.ApiClient
import ca.sheridancollege.jamsy.data.datasource.remote.DiscoveryRequest
import ca.sheridancollege.jamsy.data.datasource.remote.JamsyApiService
import ca.sheridancollege.jamsy.data.mappers.TrackMapper
import ca.sheridancollege.jamsy.domain.models.SongAction
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.domain.models.TrackActionRequest
import ca.sheridancollege.jamsy.domain.repository.TrackRepository as TrackRepositoryInterface
import ca.sheridancollege.jamsy.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for track-related operations.
 * Handles track search, discovery, liked tracks, and track actions.
 */
class TrackRepository : TrackRepositoryInterface {
    
    private val apiService: JamsyApiService = ApiClient.jamsyApiService

    /**
     * Retrieves a list of tracks from the Jamsy API without authentication.
     *
     * @return Resource containing a list of Track objects or error message
     */
    override suspend fun getTracks(): Resource<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = DiscoveryRequest(
                    seedArtists = emptyList(),
                    workout = "general"
                )
                val response = apiService.getDiscoveryTracks(requestBody, "")
                
                if (response.isSuccessful && response.body() != null) {
                    val tracksDto = response.body()!!
                    // Use type-safe mapper to convert DTOs to domain models
                    val tracks = TrackMapper.toDomainModelList(tracksDto.tracks)
                    Resource.Success(tracks)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Resource.Error("Failed to get discovery tracks: $errorBody")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to load tracks: ${e.message}")
            }
        }
    }

    /**
     * Likes a track by sending a track action to the API.
     *
     * @param track The track to like
     * @param authToken The authentication token for the API request
     * @return Resource indicating success or failure of the like operation
     */
    override suspend fun likeTrack(track: Track, authToken: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val songAction = SongAction(
                    isrc = track.isrc ?: "",
                    songName = track.name,
                    artist = track.artists.firstOrNull() ?: "",
                    action = "like",
                    genres = track.genres ?: emptyList()
                )
                val result = handleTrackAction(songAction, authToken)
                if (result.isSuccess) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(result.exceptionOrNull()?.message ?: "Failed to like track")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to like track: ${e.message}")
            }
        }
    }

    /**
     * Unlikes a track by sending a track action to the API.
     *
     * @param track The track to unlike
     * @param authToken The authentication token for the API request
     * @return Resource indicating success or failure of the unlike operation
     */
    override suspend fun unlikeTrack(track: Track, authToken: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val songAction = SongAction(
                    isrc = track.isrc ?: "",
                    songName = track.name,
                    artist = track.artists.firstOrNull() ?: "",
                    action = "dislike",
                    genres = track.genres ?: emptyList()
                )
                val result = handleTrackAction(songAction, authToken)
                if (result.isSuccess) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(result.exceptionOrNull()?.message ?: "Failed to unlike track")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to unlike track: ${e.message}")
            }
        }
    }

    /**
     * Get liked tracks for the current user.
     * @param authToken The authentication token
     * @return Resource containing a list of liked tracks or error message
     */
    override suspend fun getLikedTracks(authToken: String): Resource<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.getLikedTracks(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    val tracksDto = response.body()!!
                    // Use type-safe mapper to convert DTOs to domain models
                    val tracks = TrackMapper.toDomainModelList(tracksDto.tracks)
                    Resource.Success(tracks)
                } else {
                    Resource.Error("Failed to get liked tracks: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to get liked tracks: ${e.message}")
            }
        }
    }
    
    /**
     * Search for tracks using various filters.
     * 
     * @param query The search query string
     * @param authToken The authentication token
     * @param excludeExplicit Whether to exclude explicit content
     * @param excludeLoveSongs Whether to exclude love songs
     * @param excludeFolk Whether to exclude folk music
     * @return Result containing list of tracks or failure
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
                    val tracksDto = response.body()!!
                    // Use type-safe mapper to convert DTOs to domain models
                    val tracks = TrackMapper.toDomainModelList(tracksDto.tracks)
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
     * Get discovery tracks with authentication.
     * 
     * @param authToken The authentication token
     * @return Result containing list of tracks or failure
     */
    suspend fun getDiscoveryTracks(authToken: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                
                val requestBody = DiscoveryRequest(
                    seedArtists = emptyList(),
                    workout = "general"
                )
                
                val response = apiService.getDiscoveryTracks(requestBody, authHeader)
                if (response.isSuccessful && response.body() != null) {
                    val tracksDto = response.body()!!
                    // Use type-safe mapper to convert DTOs to domain models
                    val tracks = TrackMapper.toDomainModelList(tracksDto.tracks)
                    Result.success(tracks)
                } else {
                    Result.failure(Exception("Failed to get discovery tracks: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Handle track action (like/dislike).
     * 
     * @param songAction The song action details
     * @param authToken The authentication token
     * @return Result containing success message or failure
     */
    suspend fun handleTrackAction(songAction: SongAction, authToken: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                
                // Convert SongAction to TrackActionRequest
                val trackActionRequest = TrackActionRequest(
                    isrc = songAction.isrc,
                    songName = songAction.songName,
                    artist = songAction.artist,
                    genres = songAction.genres?.joinToString(",") ?: "",
                    action = songAction.action
                )
                
                val response = apiService.handleTrackAction(trackActionRequest, authHeader)
                
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    val status = responseBody["status"]
                    val message = responseBody["message"] ?: "Success"
                    
                    if (status == "success") {
                        Result.success(message)
                    } else {
                        Result.failure(Exception("Track action failed: $message"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to handle track action: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}