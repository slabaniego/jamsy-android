package ca.sheridancollege.jamsy.data


import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AuthManager handles authentication state and token management.
 * This is a simple wrapper around Firebase Auth for now.
 */
@Singleton
class AuthManager @Inject constructor(context: Context) {
    private val firebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

}