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
        // Launch the export operation in a coroutine to avoid blocking the UI thread
        viewModelScope.launch {
            try {
                // Retrieve the current playlist state from the ViewModel
                val currentState = _playlistState.value

                // Extract tracks from the state - only proceed if we have successful data
                val tracks = when (currentState) {
                    is Resource.Success -> currentState.data
                    else -> emptyList() // Return empty list for loading/error states
                }

                // Validate that we have tracks to export
                if (tracks.isEmpty()) {
                    onError("No tracks available to export")
                    return@launch // Exit early if no tracks
                }

                // Call the repository to create the playlist on Spotify via our API
                val result = playlistRepository.createPlaylist(authToken, tracks)

                // Handle the result of the playlist creation
                if (result.isSuccess) {
                    // Extract the playlist URL from the successful result
                    val playlistUrl = result.getOrNull() ?: ""
                    onSuccess(playlistUrl) // Notify caller of success with the URL
                } else {
                    // Extract error message from the failed result
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    onError("Failed to create playlist: $error")
                }
            } catch (e: Exception) {
                // Catch any unexpected exceptions during the export process
                onError("Failed to create playlist: ${e.message}")
            }
        }
    }

    fun restartDiscoveryFlow() {
        DiscoveryDataStore.clear()
        _playlistState.value = Resource.Loading
    }
}
