package ca.sheridancollege.jamsy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

import ca.sheridancollege.jamsy.data.repository.JamsyRepository
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.util.Resource

@HiltViewModel
class LikedTracksViewModel @Inject constructor(
    private val repository: JamsyRepository
) : ViewModel() {

    private val _likedTracksState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val likedTracksState: StateFlow<Resource<List<Track>>> = _likedTracksState.asStateFlow()

    private val _playlistPreviewState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val playlistPreviewState: StateFlow<Resource<List<Track>>> = _playlistPreviewState.asStateFlow()
    
    private val _playlistCreationState = MutableStateFlow<Resource<Map<String, String>>>(Resource.Loading)
    val playlistCreationState: StateFlow<Resource<Map<String, String>>> = _playlistCreationState.asStateFlow()

    fun loadLikedTracks(authToken: String) {
        viewModelScope.launch {
            _likedTracksState.value = Resource.Loading
            try {
                val result = repository.getLikedTracks(authToken)
                _likedTracksState.value = if (result.isSuccess) {
                    Resource.Success(result.getOrNull() ?: emptyList())
                } else {
                    Resource.Error(result.exceptionOrNull()?.message ?: "Failed to load liked tracks")
                }
            } catch (e: Exception) {
                _likedTracksState.value = Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun previewPlaylist(authToken: String) {
        viewModelScope.launch {
            _playlistPreviewState.value = Resource.Loading
            try {
                val result = repository.previewPlaylist(authToken)
                _playlistPreviewState.value = if (result.isSuccess) {
                    Resource.Success(result.getOrNull() ?: emptyList())
                } else {
                    Resource.Error(result.exceptionOrNull()?.message ?: "Failed to preview playlist")
                }
            } catch (e: Exception) {
                _playlistPreviewState.value = Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    fun createPlaylist(authToken: String) {
        viewModelScope.launch {
            _playlistCreationState.value = Resource.Loading
            try {
                val result = repository.createPlaylist(authToken)
                _playlistCreationState.value = if (result.isSuccess) {
                    Resource.Success(result.getOrNull() ?: emptyMap())
                } else {
                    Resource.Error(result.exceptionOrNull()?.message ?: "Failed to create playlist")
                }
            } catch (e: Exception) {
                _playlistCreationState.value = Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}