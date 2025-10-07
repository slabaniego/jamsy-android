package ca.sheridancollege.jamsy.data.datasource.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

import ca.sheridancollege.jamsy.data.datasource.remote.dto.ArtistsResponseDto
import ca.sheridancollege.jamsy.data.datasource.remote.dto.TracksResponseDto
import ca.sheridancollege.jamsy.domain.models.PlaylistTemplate
import ca.sheridancollege.jamsy.domain.models.PreviewPlaylistRequest
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.domain.models.TrackActionRequest

interface JamsyApiService {
    
    // Authentication - FIXED: Match backend endpoints
    @POST("api/auth/token")
    @FormUrlEncoded
    suspend fun exchangeCodeForToken(
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Response<SpotifyAuthWrapper>

    @GET("api/auth/refresh")
    suspend fun refreshToken(
        @Query("refresh_token") refreshToken: String
    ): Response<SpotifyAuthResponse>

    // Playlist Templates - FIXED: Match backend endpoints
    @GET("api/spotify/templates")
    suspend fun getPlaylistTemplates(
        @Header("Authorization") authHeader: String
    ): Response<List<PlaylistTemplate>>

    // Get recommended tracks based on a specific template - FIXED: Match backend
    @GET("api/spotify/recommend/template/{name}")
    suspend fun getPlaylistByTemplate(
        @Path("name") name: String,
        @Query("accessToken") accessToken: String
    ): Response<List<Track>>

    // Artists by workout and mood - FIXED: Match backend endpoints
    @GET("api/spotify/artists/workout/{workout}/mood/{mood}")
    suspend fun getArtistsByWorkout(
        @Path("workout") workout: String,
        @Path("mood") mood: String,
        @Header("Authorization") authHeader: String
    ): Response<ArtistsResponseDto>

    // Submit selected artists and get discovery tracks - FIXED: Use mobile API
    @POST("api/discover")
    suspend fun submitArtistSelection(
        @Body requestBody: DiscoveryRequest,
        @Header("Authorization") authHeader: String
    ): Response<TracksResponseDto>

    // Get discovery tracks - FIXED: Match backend endpoints
    @POST("api/discover")
    suspend fun getDiscoveryTracks(
        @Body requestBody: DiscoveryRequest,
        @Header("Authorization") authHeader: String
    ): Response<TracksResponseDto>

    // Handle track actions (like/dislike) - FIXED: Match backend endpoints
    @POST("api/track/action")
    suspend fun handleTrackAction(
        @Body songAction: TrackActionRequest,
        @Header("Authorization") authHeader: String
    ): Response<Map<String, String>>

    // Get liked tracks - FIXED: Match backend endpoints
    @GET("api/liked")
    suspend fun getLikedTracks(
        @Header("Authorization") authHeader: String
    ): Response<TracksResponseDto>

    // Search tracks - FIXED: Match backend endpoints
    @GET("api/search")
    suspend fun searchTracks(
        @Query("query") query: String,
        @Query("excludeExplicit") excludeExplicit: Boolean = true,
        @Query("excludeLoveSongs") excludeLoveSongs: Boolean = false,
        @Query("excludeFolk") excludeFolk: Boolean = false,
        @Header("Authorization") authHeader: String
    ): Response<TracksResponseDto>

    // Preview playlist - FIXED: Match backend endpoints (POST with liked tracks)
    @POST("api/spotify/preview-playlist")
    suspend fun getPreviewPlaylist(
        @Header("Authorization") authHeader: String,
        @Body requestBody: PreviewPlaylistRequest
    ): Response<TracksResponseDto>

    // Create playlist - FIXED: Match backend endpoints (PR #39)
    @POST("api/spotify/create-playlist")
    suspend fun createPlaylist(
        @Header("Authorization") authHeader: String,
        @Body requestBody: CreatePlaylistRequest
    ): Response<Map<String, String>>

    // Get recommendations - FIXED: Match backend endpoints
    @GET("api/spotify/recommend/template/{name}")
    suspend fun getRecommendations(
        @Path("name") templateName: String,
        @Query("accessToken") accessToken: String,
        @Header("Authorization") authHeader: String
    ): Response<List<Track>>
}