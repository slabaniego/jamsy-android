package ca.sheridancollege.jamsy.domain.repository

import ca.sheridancollege.jamsy.domain.models.User
import ca.sheridancollege.jamsy.util.Resource
import android.content.Context
import android.net.Uri

/**
 * Repository interface for user operations.
 * Defines the contract for user-related data operations.
 */
interface UserRepository {
    
    /**
     * Get the current user's profile.
     * @return Resource containing the user profile or error message
     */
    suspend fun getUserProfile(): Resource<User>
    
    /**
     * Upload a profile image for the current user.
     * @param context The application context
     * @param imageUri The URI of the image to upload
     * @return Resource containing the image URL or error message
     */
    suspend fun uploadProfileImage(context: Context, imageUri: Uri): Resource<String>
    
    /**
     * Update the user's profile information.
     * @param user The updated user information
     * @return Resource indicating success or failure
     */
    suspend fun updateUserProfile(user: User): Resource<Unit>
}
