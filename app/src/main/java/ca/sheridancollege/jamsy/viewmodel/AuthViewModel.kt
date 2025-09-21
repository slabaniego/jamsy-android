package ca.sheridancollege.jamsy.viewmodel
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ca.sheridancollege.jamsy.repository.AuthRepository
import ca.sheridancollege.jamsy.util.Resource

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _loginState = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginState: StateFlow<Resource<FirebaseUser>?> = _loginState

    private val _signupState = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signupState: StateFlow<Resource<FirebaseUser>?> = _signupState

    private val _authState = MutableStateFlow<Resource<FirebaseUser?>>(Resource.Loading)
    val authState: StateFlow<Resource<FirebaseUser?>> = _authState

    val currentUser get() = repository.currentUser
    
    // Get Spotify access token
    fun getSpotifyAccessToken(): String? = repository.getSpotifyAccessToken()

    init {
        updateAuthState()
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            updateAuthState(firebaseAuth.currentUser)
        }
    }

    private fun updateAuthState(user: FirebaseUser? = repository.currentUser) {
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
            val result = repository.login(email, password)
            _loginState.value = result
        }
    }

    fun signup(email: String, password: String) {
        viewModelScope.launch {
            _signupState.value = Resource.Loading
            val result = repository.signup(email, password)
            _signupState.value = result
        }
    }

    fun logout() {
        repository.logout()
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
                val result = repository.loginWithSpotify(code)
                _loginState.value = result
            } catch (e: Exception) {
                _loginState.value = Resource.Error("Spotify authentication failed: ${e.message}")
                Log.e("AuthViewModel", "Spotify auth error", e)
            }
        }
    }
}