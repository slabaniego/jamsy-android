package ca.sheridancollege.jamsy.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

import java.io.ByteArrayOutputStream

import ca.sheridancollege.jamsy.domain.models.User
import ca.sheridancollege.jamsy.domain.repository.UserRepository as UserRepositoryInterface
import ca.sheridancollege.jamsy.util.Resource

class UserRepository : UserRepositoryInterface {
    companion object {
        private const val TAG = "UserRepository"
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Retrieves the current user's profile from Firestore.
     * Creates a new user document if one doesn't exist.
     *
     * @return Resource containing the User object or error message
     */
    override suspend fun getUserProfile(): Resource<User> {
        return try {
            val currentUser = auth.currentUser ?: return Resource.Error("User not logged in")
            val userDoc = firestore.collection("users").document(currentUser.uid).get().await()

            if (userDoc.exists()) {
                Resource.Success(userDoc.toObject(User::class.java) ?: User(currentUser.uid, currentUser.email ?: ""))
            } else {
                val newUser = User(currentUser.uid, currentUser.email ?: "")
                firestore.collection("users").document(currentUser.uid).set(newUser).await()
                Resource.Success(newUser)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user profile", e)
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    /**
     * Uploads and processes a profile image, converting it to Base64 format.
     *
     * @param context The Android context
     * @param imageUri The URI of the image to upload
     * @return Resource containing the Base64 encoded image string or error message
     */
    override suspend fun uploadProfileImage(context: Context, imageUri: Uri): Resource<String> {
        return try {
            Log.d(TAG, "Starting profile image encoding")
            val currentUser = auth.currentUser ?: return Resource.Error("User not logged in")

            try {
                // Convert image to Base64
                val base64Image = convertImageToBase64(context, imageUri)
                Log.d(TAG, "Image encoded to Base64 successfully")

                // Update user profile with Base64 image
                firestore.collection("users").document(currentUser.uid)
                    .update("profileImageBase64", base64Image).await()
                Log.d(TAG, "Updated user profile with Base64 image")

                Resource.Success(base64Image)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing image", e)
                Resource.Error("Image processing failed: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in uploadProfileImage", e)
            Resource.Error("An unexpected error occurred: ${e.message}")
        }
    }

    private suspend fun convertImageToBase64(context: Context, imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            // Open an input stream from the URI
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: throw IllegalArgumentException("Could not open input stream for URI: $imageUri")

            try {
                // Decode the image to bitmap
                val originalBitmap = BitmapFactory.decodeStream(inputStream)

                // Resize the bitmap to a manageable size (Firestore document limit is 1MB)
                val maxDimension = 500 // Limit to 500px max dimension
                val scaledBitmap = resizeBitmap(originalBitmap, maxDimension)

                // Convert to byte array with compression
                val outputStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val byteArray = outputStream.toByteArray()

                // If not the same bitmap, recycle the original to free memory
                if (scaledBitmap != originalBitmap) {
                    originalBitmap.recycle()
                }

                // Convert to Base64
                Base64.encodeToString(byteArray, Base64.DEFAULT)
            } finally {
                inputStream.close()
            }
        }
    }

    /**
     * Update the user's profile information.
     * @param user The updated user information
     * @return Resource indicating success or failure
     */
    override suspend fun updateUserProfile(user: User): Resource<Unit> {
        return try {
            val currentUser = auth.currentUser ?: return Resource.Error("User not logged in")
            
            val userData = hashMapOf(
                "email" to user.email,
                "profileImageBase64" to user.profileImageBase64
            )
            
            firestore.collection("users").document(currentUser.uid)
                .set(userData)
                .await()
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile", e)
            Resource.Error("Failed to update profile: ${e.message}")
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // If already smaller than max, return as is
        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}