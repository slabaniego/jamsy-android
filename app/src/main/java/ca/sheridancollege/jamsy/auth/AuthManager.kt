package ca.sheridancollege.jamsy.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages authentication tokens and user session state
 */
class AuthManager(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()
    
    init {
        // Check if user is already authenticated
        val savedToken = prefs.getString("auth_token", null)
        if (savedToken != null) {
            _authToken.value = savedToken
            _isAuthenticated.value = true
        }
    }
    
    /**
     * Save authentication token
     */
    fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
        _authToken.value = token
        _isAuthenticated.value = true
    }
    
    /**
     * Get current authentication token
     */
    fun getCurrentToken(): String? {
        return _authToken.value
    }
    
    /**
     * Clear authentication data
     */
    fun clearAuth() {
        prefs.edit().remove("auth_token").apply()
        _authToken.value = null
        _isAuthenticated.value = false
    }
    
    /**
     * Check if user is authenticated
     */
    fun isUserAuthenticated(): Boolean {
        return _isAuthenticated.value && _authToken.value != null
    }
}
