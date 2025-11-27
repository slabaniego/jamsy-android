package ca.sheridancollege.jamsy.presentation.viewmodels

import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

import ca.sheridancollege.jamsy.data.AuthManager
import ca.sheridancollege.jamsy.domain.repository.TrackRepository
import ca.sheridancollege.jamsy.domain.repository.UserRepository
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.domain.models.User
import ca.sheridancollege.jamsy.util.Resource

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
    private val userRepository: UserRepository,
    authManager: AuthManager
) : BaseViewModel(authManager) {

    private val _tracksState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    private val _userProfileState = MutableStateFlow<Resource<User>>(Resource.Loading)
    val userProfileState: StateFlow<Resource<User>> = _userProfileState
    private val _currentTrackIndex = MutableStateFlow(0)

    init {
        fetchTracksFromBackend()
        loadUserProfile()
    }

    fun fetchTracksFromBackend() {
        _tracksState.value = Resource.Loading
        viewModelScope.launch {
            val result = trackRepository.getTracks()
            _tracksState.value = result
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _userProfileState.value = userRepository.getUserProfile()
        }
    }

    fun clearData() {
        _tracksState.value = Resource.Loading
        _currentTrackIndex.value = 0
    }

    fun clearUserProfile() {
        _userProfileState.value = Resource.Loading
    }
}