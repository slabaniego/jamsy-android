package ca.sheridancollege.jamsy.network

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SpotifyApiService {
    @FormUrlEncoded
    @POST("api/auth/token")
    suspend fun exchangeCodeForToken(
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): SpotifyAuthResponse
}
