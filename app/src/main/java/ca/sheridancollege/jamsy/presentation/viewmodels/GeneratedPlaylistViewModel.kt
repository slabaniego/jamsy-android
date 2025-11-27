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
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.util.Resource

@HiltViewModel
class GeneratedPlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepositoryImpl
) : ViewModel() {

    private val _playlistState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val playlistState: StateFlow<Resource<List<Track>>> = _playlistState.asStateFlow()

    fun loadGeneratedPlaylist(authToken: String) {
        viewModelScope.launch {
            _playlistState.value = Resource.Loading
            
            try {
                // Get liked tracks from DiscoveryDataStore
                val likedTracks = DiscoveryDataStore.likedTracks.value
                
                if (likedTracks.isEmpty()) {
                    _playlistState.value = Resource.Error("No liked tracks available. Please like some songs first!")
                    return@launch
                }
                
                // Call the preview-playlist endpoint with liked tracks
                val result = playlistRepository.getPreviewPlaylist(authToken, likedTracks)
                
                if (result.isSuccess) {
                    val tracks = result.getOrNull() ?: emptyList()
                    _playlistState.value = Resource.Success(tracks)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    _playlistState.value = Resource.Error("Failed to load generated playlist: $error")
                }
            } catch (e: Exception) {
                _playlistState.value = Resource.Error("Failed to load generated playlist: ${e.message}")
            }
        }
    }

    fun exportToSpotify(authToken: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Get the current playlist tracks
                val currentState = _playlistState.value
                val tracks = when (currentState) {
                    is Resource.Success -> currentState.data
                    else -> emptyList()
                }
                
                if (tracks.isEmpty()) {
                    onError("No tracks available to export")
                    return@launch
                }
                
                val result = playlistRepository.createPlaylist(authToken, tracks)
                if (result.isSuccess) {
                    val playlistUrl = result.getOrNull() ?: ""
                    onSuccess(playlistUrl)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    onError("Failed to create playlist: $error")
                }
            } catch (e: Exception) {
                onError("Failed to create playlist: ${e.message}")
            }
        }
    }

    fun restartDiscoveryFlow() {
        DiscoveryDataStore.clear()
        _playlistState.value = Resource.Loading
    }
}
