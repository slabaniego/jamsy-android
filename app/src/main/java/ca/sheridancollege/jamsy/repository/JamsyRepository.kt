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
                val authHeader = "Bearer $authToken"
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
     * Submit artist selection - matches /select-artists/submit
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
                val response = apiService.submitArtistSelection(
                    selectedArtistIds = selectedArtistIds,
                    artistNamesJson = artistNamesJson,
                    workout = workout,
                    mood = mood,
                    action = action,
                    authHeader = authHeader
                )
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to submit selection: ${response.errorBody()?.string()}"))
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
                val response = apiService.previewPlaylist(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    val tracksMap = response.body()!!
                    @Suppress("UNCHECKED_CAST")
                    val tracks = tracksMap["tracks"] as? List<Track> ?: emptyList()
                    Result.success(tracks)
                } else {
                    Result.failure(Exception("Failed to preview playlist: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get discovery tracks - matches /discover
     */
    suspend fun getDiscoveryTracks(authToken: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.getDiscoveryTracks(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to get discovery tracks: ${response.errorBody()?.string()}"))
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
                val authHeader = "Bearer $authToken"
                
                // Convert SongAction to Map<String, Any> to match backend expectations
                val payload = mapOf(
                    "isrc" to (songAction.isrc ?: ""),
                    "songName" to songAction.songName,
                    "artist" to songAction.artist,
                    "genres" to (songAction.genres?.joinToString(",") ?: ""),
                    "action" to songAction.action
                )
                
                val response = apiService.handleTrackAction(payload, authHeader)
                if (response.isSuccessful && response.body() != null) {
                    val message = response.body()?.get("message") ?: "Success"
                    Result.success(message)
                } else {
                    Result.failure(Exception("Failed to handle track action: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get tracks - this endpoint doesn't exist in Java backend
     * Use searchTracks() or other available endpoints instead
     */
    suspend fun getTracks(): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            Result.failure(Exception("This endpoint is not available. Use searchTracks() or other available endpoints instead."))
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
                val response = apiService.createPlaylist(authHeader)
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