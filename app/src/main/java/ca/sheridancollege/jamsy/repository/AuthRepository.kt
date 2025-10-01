package ca.sheridancollege.jamsy.repository
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.network.SpotifyAuthResponse
import java.io.IOException
import android.util.Log
import ca.sheridancollege.jamsy.repository.JamsyRepository

class AuthRepository(private val context: Context) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val jamsyRepository = JamsyRepository()
    private val TAG = "AuthRepository"
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private var spotifyAccessToken: String? = null
    
    init {
        // Load saved token on initialization
        spotifyAccessToken = prefs.getString("spotify_access_token", null)
        Log.d(TAG, "Loaded saved token: ${spotifyAccessToken?.take(10)}...")
    }

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun loginWithSpotify(code: String): Resource<FirebaseUser> {
        return try {
            val result = jamsyRepository.exchangeCodeForToken(code, "jamsy://callback")
            
            if (result.isFailure) {
                return Resource.Error("Authentication failed: ${result.exceptionOrNull()?.message}")
            }

            val response = result.getOrThrow()
            Log.d(TAG, "Received response: accessToken='${response.accessToken}', tokenType='${response.tokenType}', firebaseToken='${response.firebaseCustomToken.take(10)}...'")
            
            if (response.firebaseCustomToken.isNullOrEmpty()) {
                return Resource.Error("Authentication failed: Server did not provide a valid Firebase token")
            }

            if (response.accessToken.isNullOrEmpty()) {
                return Resource.Error("Authentication failed: Server did not provide a valid Spotify access token")
            }

            spotifyAccessToken = response.accessToken
            // Save token to SharedPreferences for persistence
            prefs.edit().putString("spotify_access_token", response.accessToken).apply()
            Log.d(TAG, "Saved Spotify access token: ${spotifyAccessToken?.take(10)}... (length: ${spotifyAccessToken?.length})")

            val authResult = firebaseAuth.signInWithCustomToken(response.firebaseCustomToken).await()
            Resource.Success(authResult.user!!)

        } catch (e: IOException) {
            val errorMsg = e.message ?: "Unknown server error"
            Resource.Error("Server error: $errorMsg")
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Unknown authentication error"
            Resource.Error("Authentication failed: $errorMsg")
        }
    }

    suspend fun signup(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    fun logout() {
        firebaseAuth.signOut()
        spotifyAccessToken = null
        // Clear saved token from SharedPreferences
        prefs.edit().remove("spotify_access_token").apply()
        Log.d(TAG, "Cleared Spotify access token")
    }

    suspend fun verifyAuth(): Resource<Boolean> {
        return try {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // Force token refresh to verify auth
                user.getIdToken(true).await()
                Resource.Success(true)
            } else {
                Resource.Error("No user is signed in")
            }
        } catch (e: Exception) {
            Resource.Error("Auth verification failed: ${e.message}")
        }
    }
    // Getter for the Spotify access token
    fun getSpotifyAccessToken(): String? {
        // If token is not in memory, try to load from SharedPreferences
        if (spotifyAccessToken == null) {
            spotifyAccessToken = prefs.getString("spotify_access_token", null)
            Log.d(TAG, "Loaded token from SharedPreferences: ${spotifyAccessToken?.take(10)}...")
        }
        println("AuthRepository: getSpotifyAccessToken() called, spotifyAccessToken = ${spotifyAccessToken?.take(10)}...")
        println("AuthRepository: spotifyAccessToken is null: ${spotifyAccessToken == null}")
        return spotifyAccessToken
    }
}