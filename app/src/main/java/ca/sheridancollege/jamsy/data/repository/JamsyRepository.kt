package ca.sheridancollege.jamsy.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import ca.sheridancollege.jamsy.data.datasource.remote.ApiClient
import ca.sheridancollege.jamsy.data.datasource.remote.CreatePlaylistRequest
import ca.sheridancollege.jamsy.data.datasource.remote.DiscoveryRequest
import ca.sheridancollege.jamsy.data.datasource.remote.JamsyApiService
import ca.sheridancollege.jamsy.data.datasource.remote.SpotifyAuthResponse
import ca.sheridancollege.jamsy.domain.models.Artist
import ca.sheridancollege.jamsy.domain.models.PlaylistTemplate
import ca.sheridancollege.jamsy.domain.models.SongAction
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.domain.models.TrackActionRequest

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
                    // The networkResponse is already deserialized by Retrofit using @SerializedName
                    Result.success(networkResponse)
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

    /**
     * Get artists by workout and mood - matches /more-artists
     */
    suspend fun getArtistsByWorkout(workout: String, mood: String, authToken: String): Result<List<Artist>> {
        return withContext(Dispatchers.IO) {
            try {
                println("JamsyRepository: getArtistsByWorkout called with authToken length: ${authToken.length}")
                println("JamsyRepository: authToken isBlank: ${authToken.isBlank()}")
                val authHeader = "Bearer $authToken"
                println("JamsyRepository: authHeader = $authHeader")
                val response = apiService.getArtistsByWorkout(workout, mood, authHeader)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to get artists: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Submit artist selection - uses mobile API /api/discover
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
                println("JamsyRepository: API response successful: ${response.isSuccessful}")
                println("JamsyRepository: API response body: ${response.body()}")
                
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    val tracks = responseBody["tracks"] ?: emptyList()
                    println("JamsyRepository: Extracted ${tracks.size} tracks from API response")
                    println("JamsyRepository: Track names: ${tracks.map { "${it.name} by ${it.artists.firstOrNull()}" }}")
                    Result.success(tracks)
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("JamsyRepository: API call failed - Error body: $errorBody")
                    Result.failure(Exception("Failed to submit selection: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get playlist templates - matches /spotify/templates
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
     * Get playlist by template - matches /spotify/recommend/template/{name}
     */
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
     * Preview playlist - matches /preview-playlist
     */
    suspend fun previewPlaylist(authToken: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.getPreviewPlaylist(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    Result.success(responseBody.tracks)
                } else {
                    Result.failure(Exception("Failed to preview playlist: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get discovery tracks - matches /api/discover
     */
    suspend fun getDiscoveryTracks(authToken: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                
                // Create empty request body for basic discovery
                val requestBody = DiscoveryRequest(
                    seedArtists = emptyList(),
                    workout = "general"
                )
                
                val response = apiService.getDiscoveryTracks(requestBody, authHeader)
                if (response.isSuccessful && response.body() != null) {
                    val tracksMap = response.body()!!
                    @Suppress("UNCHECKED_CAST")
                    val tracks = tracksMap["tracks"] ?: emptyList()
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
     * Get preview playlist - matches /api/preview-playlist
     */
    suspend fun getPreviewPlaylist(authToken: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.getPreviewPlaylist(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    Result.success(responseBody.tracks)
                } else {
                    Result.failure(Exception("Failed to get preview playlist: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Create playlist in Spotify - matches /api/create-playlist
     */
    suspend fun createPlaylist(authToken: String, tracks: List<Track>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val requestBody = CreatePlaylistRequest(tracks = tracks)
                val response = apiService.createPlaylist(authHeader, requestBody)
                if (response.isSuccessful && response.body() != null) {
                    val playlistUrl = response.body()!!["playlistUrl"] ?: ""
                    Result.success(playlistUrl)
                } else {
                    Result.failure(Exception("Failed to create playlist: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Handle track action - matches /api/track/action
     */
    suspend fun handleTrackAction(songAction: SongAction, authToken: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                println("JamsyRepository: handleTrackAction called")
                println("JamsyRepository: SongAction - ISRC: ${songAction.isrc}, Song: ${songAction.songName}, Artist: ${songAction.artist}, Action: ${songAction.action}")
                println("JamsyRepository: AuthToken length: ${authToken.length}")
                
                val authHeader = "Bearer $authToken"
                println("JamsyRepository: AuthHeader: $authHeader")
                
                // Convert SongAction to TrackActionRequest to match backend expectations
                val trackActionRequest = TrackActionRequest(
                    isrc = songAction.isrc,
                    songName = songAction.songName,
                    artist = songAction.artist,
                    genres = songAction.genres?.joinToString(",") ?: "",
                    action = songAction.action
                )
                
                println("JamsyRepository: TrackActionRequest: $trackActionRequest")
                println("JamsyRepository: Calling apiService.handleTrackAction...")
                
                val response = apiService.handleTrackAction(trackActionRequest, authHeader)
                println("JamsyRepository: Response received - Code: ${response.code()}, Success: ${response.isSuccessful}")
                
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    println("JamsyRepository: Response body: $responseBody")
                    val message = responseBody["message"] ?: "Success"
                    println("JamsyRepository: Track action successful! Message: $message")
                    Result.success(message)
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("JamsyRepository: Track action failed - Error body: $errorBody")
                    Result.failure(Exception("Failed to handle track action: $errorBody"))
                }
            } catch (e: Exception) {
                println("JamsyRepository: Exception in handleTrackAction: ${e.message}")
                println("JamsyRepository: Exception type: ${e.javaClass.simpleName}")
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    /**
     * Get discovery tracks - matches /api/discover
     */
    suspend fun getTracks(): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                println("JamsyRepository: Calling /api/discover endpoint...")
                // Use the /api/discover endpoint which doesn't require authentication
                val requestBody = DiscoveryRequest(
                    seedArtists = emptyList(),
                    workout = "general"
                )
                val response = apiService.getDiscoveryTracks(requestBody, "")
                println("JamsyRepository: Response code: ${response.code()}")
                println("JamsyRepository: Response successful: ${response.isSuccessful}")
                
                if (response.isSuccessful && response.body() != null) {
                    val tracksMap = response.body()!!
                    println("JamsyRepository: Response body: $tracksMap")
                    @Suppress("UNCHECKED_CAST")
                    val tracks = tracksMap["tracks"] ?: emptyList()
                    println("JamsyRepository: Extracted ${tracks.size} tracks")
                    Result.success(tracks)
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("JamsyRepository: Error response: $errorBody")
                    Result.failure(Exception("Failed to get discovery tracks: $errorBody"))
                }
            } catch (e: Exception) {
                println("JamsyRepository: Exception in getTracks: ${e.message}")
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    /**
     * Get liked tracks - matches /liked
     */
    suspend fun getLikedTracks(authToken: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.getLikedTracks(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to get liked tracks: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Create playlist - matches /create-playlist
     */
    suspend fun createPlaylist(authToken: String): Result<Map<String, String>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val requestBody = CreatePlaylistRequest(tracks = emptyList())
                val response = apiService.createPlaylist(authHeader, requestBody)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to create playlist: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}