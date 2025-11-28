/*
 * GeneratedPlaylistViewModel.kt
 * ViewModel for loading preview playlists and exporting to Spotify.
 *
 * Author: Iurii Manastyrskyi
 */
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

    /**
     * Exports the current generated playlist to Spotify.
     *
     * This function takes the tracks from the current playlist state and creates a new
     * playlist on the user's Spotify account through the Jamsy API.
     *
     * @param authToken The Spotify access token for API authentication
     * @param onSuccess Callback invoked with the created playlist's Spotify URL when successful
     * @param onError Callback invoked with an error message if the export fails
     */
    fun exportToSpotify(authToken: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val tracks = extractTracksFromState()

                if (!validateTracksForExport(tracks, onError)) {
                    return@launch
                }

                val result = createPlaylistOnSpotify(authToken, tracks)
                handlePlaylistCreationResult(result, onSuccess, onError)
            } catch (e: Exception) {
                onError("Failed to create playlist: ${e.message}")
            }
        }
    }

    private fun validateTracksForExport(tracks: List<Track>, onError: (String) -> Unit): Boolean {
        if (tracks.isEmpty()) {
            onError("No tracks available to export")
            return false
        }
        return true
    }

    private fun extractTracksFromState(): List<Track> {
        val currentState = _playlistState.value
        return when (currentState) {
            is Resource.Success -> currentState.data
            else -> emptyList() // Return empty list for loading/error states
        }
    }

    private suspend fun createPlaylistOnSpotify(authToken: String, tracks: List<Track>): Result<String> {
        return playlistRepository.createPlaylist(authToken, tracks)
    }

    private fun handlePlaylistCreationResult(
        result: Result<String>,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (result.isSuccess) {
            val playlistUrl = result.getOrNull() ?: ""
            onSuccess(playlistUrl)
        } else {
            val error = result.exceptionOrNull()?.message ?: "Unknown error"
            onError("Failed to create playlist: $error")
        }
    }

    fun restartDiscoveryFlow() {
        DiscoveryDataStore.clear()
        _playlistState.value = Resource.Loading
    }
}
