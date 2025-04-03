package ca.sheridancollege.jamsy.api

import android.util.Log
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.model.TracksResponse
import ca.sheridancollege.jamsy.network.StringConverterFactory
import ca.sheridancollege.jamsy.util.Resource
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TrackApiClient {
    companion object {
        val instance = TrackApiClient()
        private const val TAG = "TrackApiClient"
    }

    private val trackService: TrackApiService
    private var authToken: String? = null

    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(StringConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(createOkHttpClient())
            .build()

        trackService = retrofit.create(TrackApiService::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    fun setAuthToken(token: String) {
        this.authToken = token
    }


    suspend fun getTracks(): Resource<List<Track>> {
        return try {
            Log.d(TAG, "Fetching recommended tracks")

            // Check if we have a valid auth token
            if (authToken == null) {
                Log.e(TAG, "No authentication token available")
                return Resource.Error("Authentication required. Please log in to your Spotify account first.")
            }


            // First get the raw response as a string
            val responseString = withContext(Dispatchers.IO) {
                val requestBuilder = okhttp3.Request.Builder()
                    .url("http://10.0.2.2:8080/api/tracks")
                // Always include the Authorization header with the token
                requestBuilder.addHeader("Authorization", "Bearer $authToken")


                val request = requestBuilder.build()

                val client = createOkHttpClient()
                val response = client.newCall(request).execute()
                // Check if response was successful
                if (!response.isSuccessful) {
                    Log.e(TAG, "Server returned error code: ${response.code}")
                    return@withContext "ERROR: Server returned ${response.code}"
                }

                val body = response.body?.string() ?: ""

                Log.d(TAG, "Raw response: $body")
                body
            }


            // If we got an error message from our withContext block
            if (responseString.startsWith("ERROR:")) {
                return Resource.Error(responseString.substring(7))
            }
            // More robust check for HTML content
            if (responseString.trim().startsWith("<!") ||
                responseString.trim().startsWith("<html") ||
                responseString.contains("<!DOCTYPE html>")) {
                Log.e(TAG, "Server returned HTML instead of JSON - user likely not authenticated")
                Log.e(TAG, "Response content: $responseString")
                return Resource.Error("Authentication required. Please log in to your Spotify account first.")
            }

            // If it looks like JSON, try to parse it
            try {
                val gson = GsonBuilder().setLenient().create()

                // Parse the response properly
                if (responseString.isBlank()) {
                    Log.e(TAG, "Empty response from server")
                    return Resource.Error("Server returned empty response. Please try again later.")
                }

                // Basic JSON format check
                val trimmedResponse = responseString.trim()
                if (!trimmedResponse.startsWith("{")) {
                    Log.e(TAG, "Response is not a JSON object: $trimmedResponse")
                    return Resource.Error("Server returned invalid data format. Please try again later.")
                }
                val response = gson.fromJson(responseString, TracksResponse::class.java)

                if (response?.tracks == null) {
                    Log.e(TAG, "Parsed response but tracks is null")
                    return Resource.Error("Server returned invalid data format. Please try again later.")
                }

                Log.d(TAG, "Successfully fetched ${response.tracks.size} tracks")
                Resource.Success(response.tracks)
            } catch (e: JsonSyntaxException) {
                Log.e(TAG, "JSON parsing error", e)
                Log.e(TAG, "Response content: $responseString")
                Resource.Error("Server returned invalid data format. Please try logging in again.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching tracks", e)
            Resource.Error("Failed to load tracks: ${e.message}")
        }
    }

    suspend fun likeTrack(track: Track): Resource<Boolean> {
        return handleTrackAction(track, "like")
    }

    suspend fun unlikeTrack(track: Track): Resource<Boolean> {
        return handleTrackAction(track, "unlike")
    }

    private suspend fun handleTrackAction(track: Track, action: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Sending $action action for track: ${track.name}")

                val jsonObject = JSONObject().apply {
                    put("isrc", track.isrc ?: track.id)
                    put("songName", track.name)
                    put("artist", track.artists.joinToString(","))
                    put("genres", track.genres?.joinToString(",") ?: "")
                    put("action", action)
                }

                val requestBody = jsonObject.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaType())

                val headers = authToken?.let {
                    mapOf("Authorization" to "Bearer $it")
                } ?: emptyMap()

                val response = trackService.handleTrackAction(headers, requestBody)

                if (response.isSuccessful) {
                    Log.d(TAG, "$action action successful")
                    Resource.Success(true)
                } else {
                    Log.e(TAG, "$action action failed with code: ${response.code()}")
                    Resource.Error("Server returned error code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during $action operation", e)
                Resource.Error("Failed to process $action: ${e.message}")
            }
        }
    }
}