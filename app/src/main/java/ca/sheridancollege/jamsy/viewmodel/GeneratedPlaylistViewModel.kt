package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.repository.JamsyRepository
import ca.sheridancollege.jamsy.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeneratedPlaylistViewModel(
    private val jamsyRepository: JamsyRepository
) : ViewModel() {

    private val _playlistState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val playlistState: StateFlow<Resource<List<Track>>> = _playlistState.asStateFlow()

    fun loadGeneratedPlaylist(authToken: String) {
        viewModelScope.launch {
            _playlistState.value = Resource.Loading
            
            try {
                println("GeneratedPlaylistViewModel: Loading generated playlist with authToken: ${if (authToken.isBlank()) "EMPTY" else "PRESENT"}")
                
                // Call the preview-playlist endpoint to get the expanded playlist
                val result = jamsyRepository.getPreviewPlaylist(authToken)
                if (result.isSuccess) {
                    val tracks = result.getOrNull() ?: emptyList()
                    println("GeneratedPlaylistViewModel: Successfully loaded ${tracks.size} tracks for playlist")
                    _playlistState.value = Resource.Success(tracks)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    println("GeneratedPlaylistViewModel: Error loading playlist: $error")
                    _playlistState.value = Resource.Error("Failed to load generated playlist: $error")
                }
            } catch (e: Exception) {
                println("GeneratedPlaylistViewModel: Exception loading playlist: ${e.message}")
                e.printStackTrace()
                _playlistState.value = Resource.Error("Failed to load generated playlist: ${e.message}")
            }
        }
    }

    fun exportToSpotify(authToken: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                println("GeneratedPlaylistViewModel: Exporting playlist to Spotify...")
                
                val result = jamsyRepository.createPlaylist(authToken, emptyList())
                if (result.isSuccess) {
                    val playlistUrl = result.getOrNull() ?: ""
                    println("GeneratedPlaylistViewModel: Successfully created playlist: $playlistUrl")
                    onSuccess(playlistUrl)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    println("GeneratedPlaylistViewModel: Error creating playlist: $error")
                    onError("Failed to create playlist: $error")
                }
            } catch (e: Exception) {
                println("GeneratedPlaylistViewModel: Exception creating playlist: ${e.message}")
                e.printStackTrace()
                onError("Failed to create playlist: ${e.message}")
            }
        }
    }
}
