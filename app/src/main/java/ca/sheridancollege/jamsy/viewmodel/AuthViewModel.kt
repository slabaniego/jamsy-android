package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val currentUser = repository.currentUser

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
    }

    fun resetState() {
        _loginState.value = null
        _signupState.value = null
    }
}