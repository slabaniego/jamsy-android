package ca.sheridancollege.jamsy.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

import androidx.core.content.edit

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.io.IOException

import ca.sheridancollege.jamsy.data.datasource.remote.SpotifyAuthResponse
import ca.sheridancollege.jamsy.domain.repository.AuthRepository as AuthRepositoryInterface
import ca.sheridancollege.jamsy.util.Resource


class AuthRepository(context: Context) : AuthRepositoryInterface {

    // Constants
    companion object {
        private const val TAG = "AuthRepository"
        private const val PREFS_NAME = "auth_prefs"
        private const val SPOTIFY_TOKEN_KEY = "spotify_access_token"
        private const val SPOTIFY_CALLBACK_URL = "jamsy://callback"
        private const val TOKEN_PREVIEW_LENGTH = 10
    }

    // Dependencies
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val spotifyAuthRepository = SpotifyAuthRepositoryImpl()
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // State
    private var spotifyAccessToken: String? = null

    init {
        loadSavedSpotifyToken()
    }

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    // Public Authentication Methods

    /**
     * Authenticates a user with email and password.
     *
     * @param email The user's email address
     * @param password The user's password
     * @return Resource containing the authenticated FirebaseUser or error message
     */
    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return executeAuthOperation {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        }
    }

    /**
     * Authenticates a user using Spotify OAuth code.
     *
     * @param code The authorization code received from Spotify
     * @return Resource containing the authenticated FirebaseUser or error message
     */
    override suspend fun loginWithSpotify(code: String): Resource<FirebaseUser> {
        return try {
            val tokenResponse = exchangeCodeForSpotifyToken(code)
            if (tokenResponse.isFailure) {
                return Resource.Error("Authentication failed: ${tokenResponse.exceptionOrNull()?.message}")
            }

            val response = tokenResponse.getOrThrow()
            validateSpotifyResponse(response)
            saveSpotifyToken(response.accessToken)
            
            // ✅ REMOVED: Pre-loading all workouts at once caused 429 rate limit errors
            // Artists will now load on-demand when user selects a workout
            println("AuthRepository: Login successful. Artists will load when workout is selected.")
            
            signInWithFirebaseToken(response.firebaseCustomToken)

        } catch (e: IOException) {
            Resource.Error("Server error: ${e.message ?: "Unknown server error"}")
        } catch (e: Exception) {
            Resource.Error("Authentication failed: ${e.message ?: "Unknown authentication error"}")
        }
    }

    /**
     * Creates a new user account with email and password.
     *
     * @param email The user's email address
     * @param password The user's password
     * @return Resource containing the created FirebaseUser or error message
     */
    override suspend fun signup(email: String, password: String): Resource<FirebaseUser> {
        return executeAuthOperation {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        }
    }

    /**
     * Signs out the current user and clears stored tokens.
     */
    override fun logout() {
        firebaseAuth.signOut()
        clearSpotifyToken()
    }


    /**
     * Retrieves the current Spotify access token.
     *
     * @return The Spotify access token if available, null otherwise
     */
    override fun getSpotifyAccessToken(): String? {
        if (spotifyAccessToken == null) {
            loadSavedSpotifyToken()
        }
        logTokenStatus()
        return spotifyAccessToken
    }

    // Private Helper Methods

    private fun loadSavedSpotifyToken() {
        spotifyAccessToken = prefs.getString(SPOTIFY_TOKEN_KEY, null)
        Log.d(TAG, "Loaded saved token: ${spotifyAccessToken?.take(TOKEN_PREVIEW_LENGTH)}...")
    }

    private suspend fun exchangeCodeForSpotifyToken(code: String): Result<SpotifyAuthResponse> {
        return spotifyAuthRepository.exchangeCodeForToken(code, SPOTIFY_CALLBACK_URL)
    }

    private fun validateSpotifyResponse(response: SpotifyAuthResponse) {
        Log.d(TAG, """
            Received response: accessToken='${response.accessToken}', 
            tokenType='${response.tokenType}', 
            firebaseToken='${response.firebaseCustomToken.take(TOKEN_PREVIEW_LENGTH)}...'
        """.trimIndent())

        require(response.firebaseCustomToken.isNotEmpty()) {
            "Authentication failed: Server did not provide a valid Firebase token"
        }

        require(response.accessToken.isNotEmpty()) {
            "Authentication failed: Server did not provide a valid Spotify access token"
        }
    }

    private fun saveSpotifyToken(token: String) {
        spotifyAccessToken = token
        prefs.edit { putString(SPOTIFY_TOKEN_KEY, token) }
        Log.d(TAG, "Saved Spotify access token: ${token.take(TOKEN_PREVIEW_LENGTH)}... (length: ${token.length})")
    }

    private suspend fun signInWithFirebaseToken(firebaseToken: String): Resource<FirebaseUser> {
        val authResult = firebaseAuth.signInWithCustomToken(firebaseToken).await()
        return Resource.Success(authResult.user!!)
    }

    private fun clearSpotifyToken() {
        spotifyAccessToken = null
        prefs.edit { remove(SPOTIFY_TOKEN_KEY) }
        Log.d(TAG, "Cleared Spotify access token")
    }

    private fun logTokenStatus() {
        Log.d(TAG, "getSpotifyAccessToken() called, spotifyAccessToken = ${spotifyAccessToken?.take(TOKEN_PREVIEW_LENGTH)}...")
        Log.d(TAG, "spotifyAccessToken is null: ${spotifyAccessToken == null}")
    }

    private suspend fun executeAuthOperation(
        operation: suspend () -> AuthResult
    ): Resource<FirebaseUser> {
        return try {
            val result = operation()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}