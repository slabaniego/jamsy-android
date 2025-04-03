package ca.sheridancollege.jamsy.api
import ca.sheridancollege.jamsy.network.SpotifyAuthResponse
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.model.TrackActionRequest
import retrofit2.Response
import retrofit2.http.*

interface JamsyApiService {

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

    @GET("api/search")
    suspend fun searchTracks(
        @Query("query") query: String,
        @Query("excludeExplicit") excludeExplicit: Boolean = true,
        @Query("excludeLoveSongs") excludeLoveSongs: Boolean = false,
        @Query("excludeFolk") excludeFolk: Boolean = false,
        @Header("Authorization") authHeader: String
    ): Response<Map<String, List<Track>>>

    @GET("api/current-track")
    suspend fun getCurrentTrack(
        @Header("Authorization") authHeader: String
    ): Response<Map<String, Any>>

    @GET("api/recommendations")
    suspend fun getRecommendations(): Response<Map<String, Any>>

    @GET("api/random-track")
    suspend fun getRandomTrack(
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
}