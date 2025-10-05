package ca.sheridancollege.jamsy.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ca.sheridancollege.jamsy.repository.AuthRepository
import ca.sheridancollege.jamsy.repository.JamsyRepository
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.auth.SpotifyOAuthHelper
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val jamsyRepository: JamsyRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginState: StateFlow<Resource<FirebaseUser>?> = _loginState

    private val _signupState = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signupState: StateFlow<Resource<FirebaseUser>?> = _signupState

    private val _authState = MutableStateFlow<Resource<FirebaseUser?>>(Resource.Loading)
    val authState: StateFlow<Resource<FirebaseUser?>> = _authState

    val currentUser get() = authRepository.currentUser
    
    // Get Spotify access token
    fun getSpotifyAccessToken(): String? = authRepository.getSpotifyAccessToken()
    
    // Spotify OAuth helper
    private val spotifyOAuthHelper = SpotifyOAuthHelper(context)

    init {
        updateAuthState()
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            updateAuthState(firebaseAuth.currentUser)
        }
    }

    private fun updateAuthState(user: FirebaseUser? = authRepository.currentUser) {
        if (user != null) {
            _authState.value = Resource.Success(user)
            Log.d("AuthViewModel", "Auth state updated: User logged in ${user.email}")
        } else {
            _authState.value = Resource.Success(null)
            Log.d("AuthViewModel", "Auth state updated: User logged out")
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            val result = authRepository.login(email, password)
            _loginState.value = result
        }
    }

    fun signup(email: String, password: String) {
        viewModelScope.launch {
            _signupState.value = Resource.Loading
            val result = authRepository.signup(email, password)
            _signupState.value = result
        }
    }

    fun logout() {
        authRepository.logout()
        _loginState.value = null
        _signupState.value = null
    }

    fun resetState() {
        _loginState.value = null
        _signupState.value = null
    }

    fun handleSpotifyRedirect(code: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            try {
                val result = authRepository.loginWithSpotify(code)
                _loginState.value = result
            } catch (e: Exception) {
                _loginState.value = Resource.Error("Spotify authentication failed: ${e.message}")
                Log.e("AuthViewModel", "Spotify auth error", e)
            }
        }
    }
    
    /**
     * Launch Spotify OAuth flow
     */
    fun launchSpotifyAuth() {
        try {
            spotifyOAuthHelper.launchSpotifyAuth()
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Failed to launch Spotify OAuth", e)
            _loginState.value = Resource.Error("Failed to launch Spotify authentication: ${e.message}")
        }
    }
    
    /**
     * Handle OAuth redirect from Spotify
     */
    fun handleOAuthRedirect(uri: android.net.Uri) {
        val code = spotifyOAuthHelper.handleRedirect(uri)
        if (code != null) {
            handleSpotifyRedirect(code)
        } else {
            _loginState.value = Resource.Error("Invalid OAuth redirect")
        }
    }
}