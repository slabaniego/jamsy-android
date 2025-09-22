package ca.sheridancollege.jamsy.network

import ca.sheridancollege.jamsy.model.Artist
import ca.sheridancollege.jamsy.model.PlaylistTemplate
import ca.sheridancollege.jamsy.model.SongAction
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.model.TrackActionRequest
import retrofit2.Response
import retrofit2.http.*

interface JamsyApiService {
    
    // Authentication - FIXED: Match backend endpoints
    @POST("api/auth/token")
    @FormUrlEncoded
    suspend fun exchangeCodeForToken(
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Response<SpotifyAuthResponse>

    @GET("api/auth/refresh")
    suspend fun refreshToken(
        @Query("refresh_token") refreshToken: String
    ): Response<Map<String, String>>
    
    // Playlist Templates - FIXED: Match backend endpoints
    @GET("spotify/templates")
    suspend fun getPlaylistTemplates(
        @Header("Authorization") authHeader: String
    ): Response<List<PlaylistTemplate>>

    // Get recommended tracks based on a specific template - FIXED: Match backend
    @GET("spotify/recommend/template/{name}")
    suspend fun getPlaylistByTemplate(
        @Path("name") name: String,
        @Query("accessToken") accessToken: String
    ): Response<List<Track>>

    // Artists by workout and mood - FIXED: Match backend endpoints
    @GET("more-artists")
    suspend fun getArtistsByWorkout(
        @Query("workout") workout: String,
        @Query("mood") mood: String,
        @Header("Authorization") authHeader: String
    ): Response<List<Artist>>

    // Submit selected artists and get discovery tracks - FIXED: Match backend
    @POST("select-artists/submit")
    @FormUrlEncoded
    suspend fun submitArtistSelection(
        @Field("selectedArtists") selectedArtistIds: List<String>,
        @Field("artistNames") artistNamesJson: String,
        @Field("workout") workout: String,
        @Field("mood") mood: String,
        @Field("action") action: String,
        @Header("Authorization") authHeader: String
    ): Response<List<Track>>

    // Get discovery tracks - FIXED: Match backend endpoints
    @GET("discover")
    suspend fun getDiscoveryTracks(
        @Header("Authorization") authHeader: String
    ): Response<List<Track>>

    // Handle track actions (like/dislike) - FIXED: Match backend endpoints
    @POST("api/track/action")
    suspend fun handleTrackAction(
        @Body songAction: Map<String, Any>,
        @Header("Authorization") authHeader: String
    ): Response<Map<String, String>>

    // Get liked tracks - FIXED: Match backend endpoints
    @GET("liked")
    suspend fun getLikedTracks(
        @Header("Authorization") authHeader: String
    ): Response<List<Track>>

    // Search tracks - FIXED: Match backend endpoints
    @GET("api/search")
    suspend fun searchTracks(
        @Query("query") query: String,
        @Query("excludeExplicit") excludeExplicit: Boolean = true,
        @Query("excludeLoveSongs") excludeLoveSongs: Boolean = false,
        @Query("excludeFolk") excludeFolk: Boolean = false,
        @Header("Authorization") authHeader: String
    ): Response<Map<String, Any>>

    // Preview playlist - FIXED: Match backend endpoints
    @GET("preview-playlist")
    suspend fun previewPlaylist(
        @Header("Authorization") authHeader: String
    ): Response<Map<String, List<Track>>>

    // Create playlist - FIXED: Match backend endpoints
    @GET("create-playlist")
    suspend fun createPlaylist(
        @Header("Authorization") authHeader: String
    ): Response<Map<String, String>>

    // Get recommendations - FIXED: Match backend endpoints
    @POST("recommend")
    suspend fun getRecommendations(
        @Header("Authorization") authHeader: String
    ): Response<List<Track>>
}