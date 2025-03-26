package ca.sheridancollege.jamsy.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import ca.sheridancollege.jamsy.util.Resource

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

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
}