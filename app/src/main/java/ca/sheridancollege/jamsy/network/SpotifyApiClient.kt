package ca.sheridancollege.jamsy.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import java.io.IOException
import ca.sheridancollege.jamsy.model.TracksResponse
import ca.sheridancollege.jamsy.config.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class SpotifyApiClient {

    private val TAG = "SpotifyApiClient"
    private val retrofitService: SpotifyApiService

    init {
        val gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(
                TracksResponse::class.java,
                SafeResponseTypeAdapter(TracksResponse::class.java)
            )
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.API_BASE_URL)
            .addConverterFactory(StringConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(createOkHttpClient())
            .build()

        retrofitService = retrofit.create(SpotifyApiService::class.java)
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

    suspend fun exchangeCodeForToken(code: String): SpotifyAuthResponse {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Exchanging code for token: $code")
                // Add content type header to ensure we're requesting JSON
                val requestBody = okhttp3.FormBody.Builder()
                    .add("code", code)
                    .add("redirect_uri", AppConfig.SPOTIFY_AUTH_REDIRECT_URI)
                    .build()

                val request = okhttp3.Request.Builder()
                    .url("${AppConfig.API_BASE_URL}api/auth/token")
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(requestBody)
                    .build()

                Log.d(TAG, "Full request URL: ${request.url}")
                Log.d(TAG, "Request headers: ${request.headers}")

                val client = createOkHttpClient()
                val okHttpResponse = client.newCall(request).execute()

                // Check if the response was successful
                if (!okHttpResponse.isSuccessful) {
                    val errorBody = okHttpResponse.body?.string() ?: "No response body"
                    Log.e(TAG, "HTTP error: ${okHttpResponse.code}, body: $errorBody")
                    throw IOException("Server returned HTTP ${okHttpResponse.code}")
                }

                val responseBody = okHttpResponse.body?.string() ?: ""
                Log.d(TAG, "Response received, length: ${responseBody.length}")
                Log.d(TAG, "Response headers: ${okHttpResponse.headers}")
                if (responseBody.trim().startsWith("<!DOCTYPE html>") ||
                    responseBody.trim().startsWith("<html")
                ) {
                    Log.e(TAG, "Server returned HTML instead of JSON")
                    Log.e(TAG, "Response content: $responseBody")
                    throw IOException("Server returned login page instead of auth tokens. Check server configuration and make sure CORS is enabled.")
                }

                try {
                    val gson = GsonBuilder().setLenient().create()
                    val authResponse = gson.fromJson(responseBody, SpotifyAuthResponse::class.java)
                    Log.d(TAG, "Token exchange successful")
                    authResponse
                } catch (e: JsonSyntaxException) {
                    Log.e(TAG, "JSON parsing error: ${e.message}")
                    Log.e(TAG, "Response content: $responseBody")
                    throw IOException("Server returned invalid JSON data. Please check server configuration.")
                }
            } catch (e: IOException) {
                Log.e(TAG, "IO error during token exchange (Ask Gemini)", e)
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error exchanging code for token", e)
                val errorMessage = e.message ?: "Unknown error"
                throw IOException("Failed to authenticate: $errorMessage")
            }
        }
    }
}
