package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import ca.sheridancollege.jamsy.auth.AuthManager
import ca.sheridancollege.jamsy.repository.JamsyRepository

/**
 * Base ViewModel that provides authentication context to all ViewModels
 */
abstract class BaseViewModel(
    protected val jamsyRepository: JamsyRepository,
    protected val authManager: AuthManager
) : ViewModel() {
    
    /**
     * Get the current authentication token
     * Returns null if user is not authenticated
     */
    protected fun getAuthToken(): String? {
        return authManager.getCurrentToken()
    }
    
    /**
     * Check if user is authenticated
     */
    protected fun isAuthenticated(): Boolean {
        return authManager.isUserAuthenticated()
    }
}
