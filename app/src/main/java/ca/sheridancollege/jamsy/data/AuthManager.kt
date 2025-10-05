package ca.sheridancollege.jamsy.data

import android.content.Context
import android.content.SharedPreferences

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import javax.inject.Inject
import javax.inject.Singleton

/**
 * AuthManager handles authentication state and token management.
 * This is a simple wrapper around Firebase Auth for now.
 */
@Singleton
class AuthManager @Inject constructor(
    private val context: Context
) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    fun isLoggedIn(): Boolean = currentUser != null

    fun getUserId(): String? = currentUser?.uid

    fun getEmail(): String? = currentUser?.email

    fun logout() {
        firebaseAuth.signOut()
        clearStoredTokens()
    }

    private fun clearStoredTokens() {
        prefs.edit().clear().apply()
    }
}