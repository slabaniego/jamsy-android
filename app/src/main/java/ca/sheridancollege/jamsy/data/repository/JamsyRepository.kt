package ca.sheridancollege.jamsy.data.repository

import java.util.concurrent.ConcurrentHashMap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import ca.sheridancollege.jamsy.data.datasource.remote.ApiClient
import ca.sheridancollege.jamsy.data.datasource.remote.CreatePlaylistRequest
import ca.sheridancollege.jamsy.data.datasource.remote.DiscoveryRequest
import ca.sheridancollege.jamsy.data.datasource.remote.JamsyApiService
import ca.sheridancollege.jamsy.data.datasource.remote.SpotifyAuthResponse
import ca.sheridancollege.jamsy.data.datasource.remote.SpotifyAuthWrapper
import ca.sheridancollege.jamsy.domain.models.Artist
import ca.sheridancollege.jamsy.domain.models.PlaylistTemplate
import ca.sheridancollege.jamsy.domain.models.PreviewPlaylistRequest
import ca.sheridancollege.jamsy.domain.models.SongAction
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.domain.models.TrackActionRequest

/**
 * Repository for handling all API calls to the Jamsy backend.
 * Follows clean architecture principles with proper error handling and coroutine usage.
 */
class JamsyRepository {
    private val apiService: JamsyApiService = ApiClient.jamsyApiService
    
    // Cache for categorized artists (similar to web session storage)
    private val categorizedArtistsCache = ConcurrentHashMap<String, List<Artist>>()
    private var cacheTimestamp: Long = 0
    private val CACHE_DURATION_MS = 30 * 60 * 1000L // 30 minutes

    // ===== AUTHENTICATION METHODS =====

    /**
     * Exchange authorization code for access tokens.
     * 
     * @param code The authorization code from Spotify
     * @param redirectUri The redirect URI used in the authorization flow
     * @return Result containing the SpotifyAuthResponse or failure
     */
    suspend fun exchangeCodeForToken(code: String, redirectUri: String) = withContext(Dispatchers.IO) {
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

    /**
     * Populate artist cache during login.
     * 
     * Similar to web version's session storage, this pre-fetches artists for all workout types
     * to avoid rate limiting during the user session.
     * 
     * @param authToken The authentication token for API calls
     * @return Result indicating success or failure
     */
    suspend fun populateArtistCache(authToken: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val workoutTypes = listOf("Cardio", "Strength Training", "Yoga", "HIIT")
                val moods = listOf("Energetic", "Powerful", "Calm", "Intense")
                
                // Pre-fetch artists for each workout type
                for (workout in workoutTypes) {
                    val mood = when (workout) {
                        "Cardio" -> "Energetic"
                        "Strength Training" -> "Powerful"
                        "Yoga" -> "Calm"
                        "HIIT" -> "Intense"
                        else -> "Energetic"
                    }
                    
                    try {
                        val result = getArtistsByWorkoutDirect(workout, mood, authToken)
                        if (result.isSuccess) {
                            val artists = result.getOrNull() ?: emptyList()
                            categorizedArtistsCache[workout] = artists
                        }
                        
                        // Add delay between requests to avoid rate limiting
                        kotlinx.coroutines.delay(2000) // 2 second delay
                    } catch (e: Exception) {
                        // Continue with other workout types even if one fails
                        println("Failed to cache artists for $workout: ${e.message}")
                    }
                }
                
                cacheTimestamp = System.currentTimeMillis()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Direct API call to get artists by workout - bypasses cache
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun getArtistsByWorkoutDirect(workout: String, mood: String, authToken: String): Result<List<Artist>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.getArtistsByWorkout(workout, mood, authHeader)
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    val artistsList = responseBody["artists"] as? List<Any> ?: emptyList()
                    
                    // Convert Map to Artist objects
                    val artists: List<Artist> = artistsList.mapNotNull { artistMap: Any? ->
                        try {
                            val artistData = artistMap as? Map<String, Any> ?: return@mapNotNull null
                            Artist(
                                id = artistData["id"] as? String ?: "",
                                name = artistData["name"] as? String ?: "",
                                imageUrl = artistData["imageUrl"] as? String ?: "",
                                genres = (artistData["genres"] as? List<Any>)?.filterIsInstance<String>() ?: emptyList(),
                                popularity = (artistData["popularity"] as? Number)?.toInt() ?: 0,
                                workoutCategories = (artistData["workoutCategories"] as? List<Any>)?.filterIsInstance<String>() ?: emptyList()
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }
                    
                    Result.success(artists)
                } else {
                    Result.failure(Exception("Failed to get artists: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ===== TRACK SEARCH AND DISCOVERY METHODS =====

    /**
     * Search for tracks using various filters.
     * 
     * @param query The search query string
     * @param authToken The authentication token for API calls
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
        excludeFolk: Boolean = false,
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
    suspend fun refreshToken(refreshToken: String) = withContext(Dispatchers.IO) {
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

    // ===== ARTIST AND PLAYLIST METHODS =====

    /**
     * Pre-load artists for all workout categories (like web version)
     * This mirrors the web flow where artists are loaded during login and stored in session
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun preloadArtistsForAllWorkouts(authToken: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                println("JamsyRepository: Pre-loading artists for all workout categories (like web version)")
                val authHeader = "Bearer $authToken"
                
                // Get artists for each workout category
                val workouts = listOf("Cardio", "Strength Training", "Yoga", "HIIT")
                val moods = listOf("Energetic", "Powerful", "Calm", "Intense")
                
                for (workout in workouts) {
                    val mood = when (workout) {
                        "Cardio" -> "Energetic"
                        "Strength Training" -> "Powerful"
                        "Yoga" -> "Calm"
                        "HIIT" -> "Intense"
                        else -> "Energetic"
                    }
                    
                    try {
                        val response = apiService.getArtistsByWorkout(workout, mood, authHeader)
                        if (response.isSuccessful && response.body() != null) {
                            val responseBody = response.body()!!
                            val artistsList = responseBody["artists"] as? List<Any> ?: emptyList()
                            println("JamsyRepository: Pre-loaded ${artistsList.size} artists for $workout")
                            
                            // Convert to Artist objects and cache
                            val artists: List<Artist> = artistsList.mapNotNull { artistMap: Any? ->
                                try {
                                    val artistData = artistMap as? Map<String, Any> ?: return@mapNotNull null
                                    Artist(
                                        id = artistData["id"] as? String ?: "",
                                        name = artistData["name"] as? String ?: "",
                                        imageUrl = artistData["imageUrl"] as? String ?: "",
                                        genres = (artistData["genres"] as? List<Any>)?.filterIsInstance<String>() ?: emptyList(),
                                        popularity = (artistData["popularity"] as? Number)?.toInt() ?: 0,
                                        workoutCategories = (artistData["workoutCategories"] as? List<Any>)?.filterIsInstance<String>() ?: emptyList()
                                    )
                                } catch (_: Exception) {
                                    null
                                }
                            }
                            
                            // Cache the artists for this workout
                            categorizedArtistsCache[workout] = artists
                        } else {
                            println("JamsyRepository: Failed to pre-load artists for $workout: ${response.errorBody()?.string()}")
                        }
                    } catch (e: Exception) {
                        println("JamsyRepository: Error pre-loading artists for $workout: ${e.message}")
                    }
                    
                    // Small delay between requests to avoid rate limiting
                    kotlinx.coroutines.delay(1000)
                }
                
                // Update cache timestamp
                cacheTimestamp = System.currentTimeMillis()
                println("JamsyRepository: Pre-loading completed. Cached artists for: ${categorizedArtistsCache.keys}")
                
                Result.success(Unit)
            } catch (e: Exception) {
                println("JamsyRepository: Error in pre-loading: ${e.message}")
                Result.failure(e)
            }
        }
    }

    /**
     * Get artists by workout and mood - with smart caching
     * ✅ FIXED: Now fetches on-demand if cache is empty or expired
     */
    suspend fun getArtistsByWorkout(workout: String, mood: String, authToken: String): Result<List<Artist>> {
        return withContext(Dispatchers.IO) {
            try {
                // Check if we have cached data and it's not expired
                val isCacheValid = (System.currentTimeMillis() - cacheTimestamp) < CACHE_DURATION_MS
                val cachedArtists = categorizedArtistsCache[workout]
                println("JamsyRepository: Cache check - valid: $isCacheValid, cached artists: ${cachedArtists?.size ?: 0}")
                
                if (isCacheValid && !cachedArtists.isNullOrEmpty()) {
                    // Use cached data
                    println("JamsyRepository: ✅ Using cached data for $workout (${cachedArtists.size} artists)")
                    val shuffledArtists = cachedArtists.shuffled().take(20)
                    return@withContext Result.success(shuffledArtists)
                }
                
                // ✅ FIXED: If no cached data or expired, fetch from API on-demand
                println("JamsyRepository: 🔄 Cache miss - fetching artists for $workout from API")
                val result = getArtistsByWorkoutDirect(workout, mood, authToken)
                
                if (result.isSuccess) {
                    val artists = result.getOrNull() ?: emptyList()
                    // Cache the results for future requests
                    categorizedArtistsCache[workout] = artists
                    cacheTimestamp = System.currentTimeMillis()
                    println("JamsyRepository: ✅ Fetched and cached ${artists.size} artists for $workout")
                    return@withContext Result.success(artists.shuffled().take(20))
                } else {
                    println("JamsyRepository: ❌ Failed to fetch artists: ${result.exceptionOrNull()?.message}")
                    return@withContext result
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
        _selectedArtistIds: List<String>,
        artistNamesJson: String,
        workout: String,
        _mood: String,
        _action: String,
        authToken: String,
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
                
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!! as Map<String, Any>
                    @Suppress("UNCHECKED_CAST")
                    val tracks = responseBody["tracks"] as? List<Track> ?: emptyList()
                    Result.success(tracks)
                } else {
                    val errorBody = response.errorBody()?.string()
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
                    val tracksMap = response.body()!! as Map<String, Any>
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
     * Create playlist in Spotify - matches /api/create-playlist
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

    // ===== TRACK ACTIONS AND USER DATA METHODS =====

    /**
     * Handle track action (like/dislike) - matches /api/track/action
     */
    suspend fun handleTrackAction(songAction: SongAction, authToken: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                
                // Convert SongAction to TrackActionRequest to match backend expectations
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

    /**
     * Get discovery tracks without authentication - matches /api/discover
     */
    suspend fun getTracks(): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = DiscoveryRequest(
                    seedArtists = emptyList(),
                    workout = "general"
                )
                val response = apiService.getDiscoveryTracks(requestBody, "")
                
                if (response.isSuccessful && response.body() != null) {
                    val tracksMap = response.body()!! as Map<String, Any>
                    @Suppress("UNCHECKED_CAST")
                    val tracks = tracksMap["tracks"] as? List<Track> ?: emptyList()
                    Result.success(tracks)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to get discovery tracks: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get liked tracks - matches /api/liked
     */
    suspend fun getLikedTracks(authToken: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $authToken"
                val response = apiService.getLikedTracks(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!! as Map<String, Any>
                    @Suppress("UNCHECKED_CAST")
                    val tracks = responseBody["tracks"] as? List<Track> ?: emptyList()
                    Result.success(tracks)
                } else {
                    Result.failure(Exception("Failed to get liked tracks: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get preview playlist - matches /api/spotify/preview-playlist (POST with liked tracks)
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun getPreviewPlaylist(authToken: String, likedTracks: List<Track>): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                println("JamsyRepository: Sending ${likedTracks.size} liked tracks to preview-playlist endpoint")
                val authHeader = "Bearer $authToken"
                val requestBody = PreviewPlaylistRequest(likedTracks)
                val response = apiService.getPreviewPlaylist(authHeader, requestBody)
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!! as Map<String, Any>
                    val tracksData = responseBody["tracks"] as? List<Map<String, Any>> ?: emptyList()
                    
                    // Convert maps to Track objects
                    val tracks = tracksData.map { trackMap ->
                        Track(
                            id = trackMap["id"] as? String,
                            name = trackMap["name"] as? String ?: "",
                            artists = (trackMap["artists"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                            albumCover = trackMap["albumCover"] as? String,
                            imageUrl = trackMap["imageUrl"] as? String,
                            previewUrl = trackMap["previewUrl"] as? String,
                            durationMs = (trackMap["durationMs"] as? Number)?.toInt() ?: 0
                        )
                    }
                    println("JamsyRepository: Received ${tracks.size} tracks from preview-playlist")
                    Result.success(tracks)
                } else {
                    Result.failure(Exception("Failed to get preview playlist: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                println("JamsyRepository: Error getting preview playlist: ${e.message}")
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    /**
     * Get recommendations by template - matches /spotify/recommend/template/{name}
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

}