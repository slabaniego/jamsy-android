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
                val response = apiService.getPreviewPlaylist(authHeader)
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
     * Get discovery tracks - matches /api/discover
     */
    suspend fun getDiscoveryTracks(authToken: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.getDiscoveryTracks(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    val tracksMap = response.body()!!
                    @Suppress("UNCHECKED_CAST")
                    val tracks = tracksMap["tracks"] as? List<Track> ?: emptyList()
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
                    val tracksMap = response.body()!!
                    @Suppress("UNCHECKED_CAST")
                    val tracks = tracksMap["tracks"] as? List<Track> ?: emptyList()
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
     * Create playlist in Spotify - matches /api/create-playlist
     */
    suspend fun createPlaylist(authToken: String, tracks: List<Track>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.createPlaylist(authHeader, tracks)
                if (response.isSuccessful && response.body() != null) {
                    val playlistUrl = response.body()!!["playlistUrl"] as? String ?: ""
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
     * Get discovery tracks - matches /api/discover
     */
    suspend fun getTracks(): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                println("JamsyRepository: Calling /api/discover endpoint...")
                // Use the /api/discover endpoint which doesn't require authentication
                val response = apiService.getDiscoveryTracks("")
                println("JamsyRepository: Response code: ${response.code()}")
                println("JamsyRepository: Response successful: ${response.isSuccessful}")
                
                if (response.isSuccessful && response.body() != null) {
                    val tracksMap = response.body()!!
                    println("JamsyRepository: Response body: $tracksMap")
                    @Suppress("UNCHECKED_CAST")
                    val tracks = tracksMap["tracks"] as? List<Track> ?: emptyList()
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
                val response = apiService.createPlaylist(authHeader, emptyList())
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