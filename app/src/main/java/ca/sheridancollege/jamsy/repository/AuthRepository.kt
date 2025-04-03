package ca.sheridancollege.jamsy.repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.network.SpotifyApiClient
import java.io.IOException
import android.util.Log
import ca.sheridancollege.jamsy.api.TrackApiClient

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val spotifyApiClient = SpotifyApiClient()
    private val TAG = "AuthRepository"
    private var spotifyAccessToken: String? = null

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

            val response = spotifyApiClient.exchangeCodeForToken(code)

            if (response.firebaseCustomToken.isNullOrEmpty()) {
                return Resource.Error("Authentication failed: Server did not provide a valid token")
            }

            spotifyAccessToken = response.accessToken
            Log.d(TAG, "Saved Spotify access token: ${spotifyAccessToken?.take(10)}...")

            // Set the token in Track API client for future requests
            TrackApiClient.instance.setAuthToken(spotifyAccessToken ?: "")


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
        return spotifyAccessToken
    }
}