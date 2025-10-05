package ca.sheridancollege.jamsy.domain.repository

import ca.sheridancollege.jamsy.util.Resource
import com.google.firebase.auth.FirebaseUser

/**
 * Repository interface for authentication operations.
 * Defines the contract for authentication-related data operations.
 */
interface AuthRepository {
    
    /**
     * Get the current authenticated user.
     * @return The current FirebaseUser if authenticated, null otherwise
     */
    val currentUser: FirebaseUser?
    
    /**
     * Authenticate a user with email and password.
     * @param email The user's email address
     * @param password The user's password
     * @return Resource containing the authenticated FirebaseUser or error message
     */
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    
    /**
     * Authenticate a user using Spotify OAuth code.
     * @param code The authorization code received from Spotify
     * @return Resource containing the authenticated FirebaseUser or error message
     */
    suspend fun loginWithSpotify(code: String): Resource<FirebaseUser>
    
    /**
     * Create a new user account with email and password.
     * @param email The user's email address
     * @param password The user's password
     * @return Resource containing the created FirebaseUser or error message
     */
    suspend fun signup(email: String, password: String): Resource<FirebaseUser>
    
    /**
     * Sign out the current user and clear stored tokens.
     */
    fun logout()
    
    /**
     * Get the current Spotify access token.
     * @return The Spotify access token if available, null otherwise
     */
    fun getSpotifyAccessToken(): String?
}
