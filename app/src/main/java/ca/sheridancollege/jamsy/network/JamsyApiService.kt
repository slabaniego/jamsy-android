package ca.sheridancollege.jamsy.network

import ca.sheridancollege.jamsy.model.Artist
import ca.sheridancollege.jamsy.model.PlaylistTemplate
import ca.sheridancollege.jamsy.model.SongAction
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.model.TrackActionRequest
import retrofit2.Response
import retrofit2.http.*

interface JamsyApiService {
    
    // Authentication
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

    // Tracks
    @GET("api/tracks")
    suspend fun getTracks(
        @Query("excludeExplicit") excludeExplicit: Boolean = true,
        @Query("excludeLoveSongs") excludeLoveSongs: Boolean = false,
        @Query("excludeFolk") excludeFolk: Boolean = false
    ): Response<Map<String, List<Track>>>

    @POST("api/track/action")
    suspend fun trackAction(
        @Body request: TrackActionRequest
    ): Response<Map<String, String>>

    @GET("api/current-track")
    suspend fun getCurrentTrack(
        @Header("Authorization") authHeader: String
    ): Response<Map<String, Any>>

    @GET("api/similar-tracks")
    suspend fun getSimilarTracks(
        @Query("trackName") trackName: String,
        @Query("artistName") artistName: String,
        @Query("excludeExplicit") excludeExplicit: Boolean = true,
        @Query("excludeLoveSongs") excludeLoveSongs: Boolean = false,
        @Query("excludeFolk") excludeFolk: Boolean = false
    ): Response<Map<String, List<Track>>>
    
    // Playlist Templates
    @GET("spotify/templates")
    suspend fun getPlaylistTemplates(
        @Header("Authorization") authHeader: String
    ): Response<List<PlaylistTemplate>>

    // Get recommended tracks based on a specific template
    @GET("spotify/recommend/template/{name}")
    suspend fun getPlaylistByTemplate(
        @Path("name") name: String,
        @Query("accessToken") accessToken: String
    ): Response<List<Track>>

    // Artists by workout and mood
    @GET("more-artists")
    suspend fun getArtistsByWorkout(
        @Query("workout") workout: String,
        @Query("mood") mood: String,
        @Header("Authorization") authHeader: String
    ): Response<List<Artist>>

    // Submit selected artists and get discovery tracks
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

    // Get discovery tracks
    @GET("discover")
    suspend fun getDiscoveryTracks(
        @Header("Authorization") authHeader: String
    ): Response<List<Track>>

    // Handle track actions (like/dislike)
    @POST("api/track/action")
    suspend fun handleTrackAction(
        @Body songAction: SongAction,
        @Header("Authorization") authHeader: String
    ): Response<Map<String, String>>

    // Get liked tracks
    @GET("liked")
    suspend fun getLikedTracks(
        @Header("Authorization") authHeader: String
    ): Response<List<Track>>

    // Search tracks
    @GET("api/search")
    suspend fun searchTracks(
        @Query("query") query: String,
        @Query("excludeExplicit") excludeExplicit: Boolean = true,
        @Query("excludeLoveSongs") excludeLoveSongs: Boolean = false,
        @Query("excludeFolk") excludeFolk: Boolean = false,
        @Header("Authorization") authHeader: String
    ): Response<Map<String, Any>>

    // Preview playlist
    @GET("preview-playlist")
    suspend fun previewPlaylist(
        @Header("Authorization") authHeader: String
    ): Response<Map<String, List<Track>>>

    // Create playlist
    @GET("create-playlist")
    suspend fun createPlaylist(
        @Header("Authorization") authHeader: String
    ): Response<Map<String, String>>

    // Get recommendations
    @POST("recommend")
    suspend fun getRecommendations(
        @Header("Authorization") authHeader: String
    ): Response<List<Track>>
}