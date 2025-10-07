package ca.sheridancollege.jamsy.presentation.viewmodels

import androidx.lifecycle.ViewModel

import ca.sheridancollege.jamsy.data.AuthManager

/**
 * Base ViewModel that provides authentication context to all ViewModels
 */
abstract class BaseViewModel(
    protected val authManager: AuthManager
) : ViewModel() {
    
    /**
     * Get the current authentication token
     * Returns null if user is not authenticated
     */
    protected fun getAuthToken(): String? {
        return authManager.currentUser?.uid
    }
    
    /**
     * Check if user is authenticated
     */
    protected fun isAuthenticated(): Boolean {
        return authManager.isLoggedIn()
    }
}
