/*
 * AuthManager.kt
 * Simple wrapper around FirebaseAuth for accessing the current user.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(context: Context) {
    private val firebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

}