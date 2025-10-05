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
class GeneratedPlaylistViewModel @Inject constructor(
    private val jamsyRepository: JamsyRepository
) : ViewModel() {

    private val _playlistState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val playlistState: StateFlow<Resource<List<Track>>> = _playlistState.asStateFlow()

    fun loadGeneratedPlaylist(authToken: String) {
        viewModelScope.launch {
            _playlistState.value = Resource.Loading
            
            try {
                println("GeneratedPlaylistViewModel: ===== LOADING GENERATED PLAYLIST =====")
                println("GeneratedPlaylistViewModel: AuthToken length: ${authToken.length}")
                println("GeneratedPlaylistViewModel: AuthToken is blank: ${authToken.isBlank()}")
                
                // Call the preview-playlist endpoint to get the expanded playlist
                val result = jamsyRepository.getPreviewPlaylist(authToken)
                println("GeneratedPlaylistViewModel: Repository call completed")
                println("GeneratedPlaylistViewModel: Result isSuccess: ${result.isSuccess}")
                
                if (result.isSuccess) {
                    val tracks = result.getOrNull() ?: emptyList()
                    println("GeneratedPlaylistViewModel: Successfully loaded ${tracks.size} tracks for playlist")
                    if (tracks.isNotEmpty()) {
                        println("GeneratedPlaylistViewModel: Track names: ${tracks.take(3).map { "${it.name} by ${it.artists.firstOrNull()}" }}")
                    } else {
                        println("GeneratedPlaylistViewModel: WARNING - No tracks returned from API")
                    }
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
                
                println("GeneratedPlaylistViewModel: Exporting ${tracks.size} tracks to Spotify")
                
                val result = jamsyRepository.createPlaylist(authToken, tracks)
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
