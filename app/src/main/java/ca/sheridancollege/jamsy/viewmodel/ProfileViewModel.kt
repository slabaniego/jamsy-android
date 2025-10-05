package ca.sheridancollege.jamsy.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ca.sheridancollege.jamsy.model.User
import ca.sheridancollege.jamsy.repository.UserRepository
import ca.sheridancollege.jamsy.util.Resource
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<Resource<User>>(Resource.Loading)
    val profileState: StateFlow<Resource<User>> = _profileState

    private val _uploadState = MutableStateFlow<Resource<String>?>(null)
    val uploadState: StateFlow<Resource<String>?> = _uploadState

    fun getUserProfile() {
        viewModelScope.launch {
            _profileState.value = Resource.Loading
            _profileState.value = repository.getUserProfile()
        }
    }

    fun uploadProfileImage(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            _uploadState.value = Resource.Loading
            _uploadState.value = repository.uploadProfileImage(context, imageUri)
        }
    }

    fun resetUploadState() {
        _uploadState.value = null
    }

    fun clearUserData() {
        _profileState.value = Resource.Loading
        _uploadState.value = null
    }

}