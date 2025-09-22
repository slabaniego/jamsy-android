package ca.sheridancollege.jamsy.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import java.security.SecureRandom
import java.util.Base64

/**
 * Helper class for Spotify OAuth authentication
 */
class SpotifyOAuthHelper(private val context: Context) {
    
    companion object {
        private const val TAG = "SpotifyOAuthHelper"
        private const val SPOTIFY_CLIENT_ID = "your_spotify_client_id" // Replace with actual client ID
        private const val REDIRECT_URI = "jamsy://callback"
        private const val SPOTIFY_AUTH_URL = "https://accounts.spotify.com/authorize"
        private const val SCOPES = "user-read-private user-read-email user-top-read user-read-recently-played playlist-modify-public playlist-modify-private"
    }
    
    /**
     * Generate Spotify OAuth URL
     */
    fun generateAuthUrl(): String {
        val state = generateRandomState()
        saveState(state)
        
        return Uri.Builder()
            .scheme("https")
            .authority("accounts.spotify.com")
            .appendPath("authorize")
            .appendQueryParameter("client_id", SPOTIFY_CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("scope", SCOPES)
            .appendQueryParameter("state", state)
            .build()
            .toString()
    }
    
    /**
     * Launch Spotify OAuth in browser
     */
    fun launchSpotifyAuth() {
        val authUrl = generateAuthUrl()
        Log.d(TAG, "Launching Spotify OAuth: $authUrl")
        
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch Spotify OAuth", e)
        }
    }
    
    /**
     * Handle OAuth redirect and extract authorization code
     */
    fun handleRedirect(uri: Uri): String? {
        val code = uri.getQueryParameter("code")
        val state = uri.getQueryParameter("state")
        val error = uri.getQueryParameter("error")
        
        Log.d(TAG, "Handling redirect - Code: ${code?.take(10)}..., State: $state, Error: $error")
        
        if (error != null) {
            Log.e(TAG, "OAuth error: $error")
            return null
        }
        
        if (code == null) {
            Log.e(TAG, "No authorization code received")
            return null
        }
        
        if (!isValidState(state)) {
            Log.e(TAG, "Invalid state parameter")
            return null
        }
        
        return code
    }
    
    /**
     * Generate random state for CSRF protection
     */
    private fun generateRandomState(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
    
    /**
     * Save state to SharedPreferences for validation
     */
    private fun saveState(state: String) {
        val prefs = context.getSharedPreferences("spotify_oauth", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("oauth_state", state)
            .putLong("oauth_state_time", System.currentTimeMillis())
            .apply()
    }
    
    /**
     * Validate state parameter
     */
    private fun isValidState(state: String?): Boolean {
        if (state == null) return false
        
        val prefs = context.getSharedPreferences("spotify_oauth", Context.MODE_PRIVATE)
        val savedState = prefs.getString("oauth_state", null)
        val stateTime = prefs.getLong("oauth_state_time", 0)
        
        // State should be valid for 10 minutes
        val isValid = savedState == state && 
                     (System.currentTimeMillis() - stateTime) < 10 * 60 * 1000
        
        if (isValid) {
            // Clear the state after successful validation
            prefs.edit().remove("oauth_state").remove("oauth_state_time").apply()
        }
        
        return isValid
    }
}
