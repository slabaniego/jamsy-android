/*
 * JamsyApiService.kt
 * Retrofit service definition for Jamsy backend API endpoints.
 *
 * Author: Iurii Manastyrskyi
 */
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
import ca.sheridancollege.jamsy.domain.models.PreviewPlaylistRequest
import ca.sheridancollege.jamsy.domain.models.TrackActionRequest

interface JamsyApiService {
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

    @GET("api/spotify/artists/workout/{workout}/mood/{mood}")
    suspend fun getArtistsByWorkout(
        @Path("workout") workout: String,
        @Path("mood") mood: String,
        @Header("Authorization") authHeader: String
    ): Response<ArtistsResponseDto>

    @POST("api/discover")
    suspend fun submitArtistSelection(
        @Body requestBody: DiscoveryRequest,
        @Header("Authorization") authHeader: String
    ): Response<TracksResponseDto>

    @POST("api/discover")
    suspend fun getDiscoveryTracks(
        @Body requestBody: DiscoveryRequest,
        @Header("Authorization") authHeader: String
    ): Response<TracksResponseDto>

    @POST("api/track/action")
    suspend fun handleTrackAction(
        @Body songAction: TrackActionRequest,
        @Header("Authorization") authHeader: String
    ): Response<Map<String, String>>

    @GET("api/liked")
    suspend fun getLikedTracks(
        @Header("Authorization") authHeader: String
    ): Response<TracksResponseDto>

    @POST("api/spotify/preview-playlist")
    suspend fun getPreviewPlaylist(
        @Header("Authorization") authHeader: String,
        @Body requestBody: PreviewPlaylistRequest
    ): Response<TracksResponseDto>

    @POST("api/spotify/create-playlist")
    suspend fun createPlaylist(
        @Header("Authorization") authHeader: String,
        @Body requestBody: CreatePlaylistRequest
    ): Response<Map<String, String>>
}