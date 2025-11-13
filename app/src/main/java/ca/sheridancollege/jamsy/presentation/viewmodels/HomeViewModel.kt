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
    val tracksState: StateFlow<Resource<List<Track>>> = _tracksState

    private val _userProfileState = MutableStateFlow<Resource<User>>(Resource.Loading)
    val userProfileState: StateFlow<Resource<User>> = _userProfileState

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex

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

    fun likeCurrentTrack() {
        val currentTracks = (_tracksState.value as? Resource.Success)?.data ?: return
        val currentIndex = _currentTrackIndex.value
        if (currentIndex < currentTracks.size) {
            viewModelScope.launch {
                val track = currentTracks[currentIndex]
                val authToken = getAuthToken() ?: return@launch
                trackRepository.likeTrack(track, authToken)
                moveToNextTrack()
            }
        }
    }

    fun unlikeCurrentTrack() {
        val currentTracks = (_tracksState.value as? Resource.Success)?.data ?: return
        val currentIndex = _currentTrackIndex.value
        if (currentIndex < currentTracks.size) {
            viewModelScope.launch {
                val track = currentTracks[currentIndex]
                val authToken = getAuthToken() ?: return@launch
                trackRepository.unlikeTrack(track, authToken)
                moveToNextTrack()
            }
        }
    }

    fun moveToNextTrack() {
        val currentTracks = (_tracksState.value as? Resource.Success)?.data ?: return
        val newIndex = _currentTrackIndex.value + 1
        if (newIndex < currentTracks.size) {
            _currentTrackIndex.value = newIndex
        }
    }

    fun clearData() {
        _tracksState.value = Resource.Loading
        _currentTrackIndex.value = 0
    }
}