package ca.sheridancollege.jamsy.api

import ca.sheridancollege.jamsy.model.TracksResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.HeaderMap

interface TrackApiService {
    @GET("api/tracks")
    suspend fun getRecommendedTracks(
        @Query("excludeExplicit") excludeExplicit: Boolean = true,
        @Query("excludeLoveSongs") excludeLoveSongs: Boolean = false,
        @Query("excludeFolk") excludeFolk: Boolean = false
    ): String

    @POST("api/track/action")
    suspend fun handleTrackAction(
        @HeaderMap headers: Map<String, String>,
        @Body requestBody: RequestBody
    ): Response<Void>
}





