package ca.sheridancollege.jamsy.data.datasource.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * Retrofit service for direct Spotify Web API calls
 * Uses Spotify's official API endpoints at https://api.spotify.com/v1/
 */
interface SpotifyApiService {
    
    /**
     * Get the current user's profile
     * Requires: user-read-private scope
     * 
     * @param authHeader Bearer token authorization header
     * @return Response containing the user's profile information
     */
    @GET("https://api.spotify.com/v1/me")
    suspend fun getCurrentUserProfile(
        @Header("Authorization") authHeader: String
    ): Response<SpotifyUserResponse>
}
