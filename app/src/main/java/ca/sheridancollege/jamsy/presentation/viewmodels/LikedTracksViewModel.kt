package ca.sheridancollege.jamsy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

import ca.sheridancollege.jamsy.data.DiscoveryDataStore
import ca.sheridancollege.jamsy.data.repository.PlaylistRepositoryImpl
import ca.sheridancollege.jamsy.data.repository.TrackRepository
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.util.Resource

@HiltViewModel
class LikedTracksViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
    private val playlistRepository: PlaylistRepositoryImpl
) : ViewModel() {

    private val _likedTracksState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val likedTracksState: StateFlow<Resource<List<Track>>> = _likedTracksState.asStateFlow()

    private val _playlistPreviewState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val playlistPreviewState: StateFlow<Resource<List<Track>>> = _playlistPreviewState.asStateFlow()
    
    private val _playlistCreationState = MutableStateFlow<Resource<String>>(Resource.Loading)
    val playlistCreationState: StateFlow<Resource<String>> = _playlistCreationState.asStateFlow()

    fun loadLikedTracks(authToken: String) {
        viewModelScope.launch {
            _likedTracksState.value = Resource.Loading
            _likedTracksState.value = trackRepository.getLikedTracks(authToken)
        }
    }

    fun previewPlaylist(authToken: String) {
        viewModelScope.launch {
            _playlistPreviewState.value = Resource.Loading
            
            // Get liked tracks from current state
            val likedTracks = when (val state = _likedTracksState.value) {
                is Resource.Success -> state.data
                else -> emptyList()
            }
            
            if (likedTracks.isEmpty()) {
                _playlistPreviewState.value = Resource.Error("No liked tracks available")
                return@launch
            }
            
            val result = playlistRepository.getPreviewPlaylist(authToken, likedTracks)
            _playlistPreviewState.value = when {
                result.isSuccess -> Resource.Success(result.getOrNull() ?: emptyList())
                else -> Resource.Error(result.exceptionOrNull()?.message ?: "Failed to preview playlist")
            }
        }
    }
    
    fun createPlaylist(authToken: String, tracks: List<Track>) {
        viewModelScope.launch {
            _playlistCreationState.value = Resource.Loading
            
            val result = playlistRepository.createPlaylist(authToken, tracks)
            _playlistCreationState.value = when {
                result.isSuccess -> Resource.Success(result.getOrNull() ?: "")
                else -> Resource.Error(result.exceptionOrNull()?.message ?: "Failed to create playlist")
            }
        }
    }

    fun restartDiscoveryFlow() {
        println("LikedTracksViewModel: Restarting discovery flow - clearing all data")
        DiscoveryDataStore.clear()
        _likedTracksState.value = Resource.Loading
        _playlistPreviewState.value = Resource.Loading
    }
}