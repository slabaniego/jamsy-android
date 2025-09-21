package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.SongAction
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.repository.JamsyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiscoveryViewModel(
    private val jamsyRepository: JamsyRepository
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex

    private val _likedTracks = MutableStateFlow<List<Track>>(emptyList())
    val likedTracks: StateFlow<List<Track>> = _likedTracks

    fun loadDiscoveryTracks(authToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = jamsyRepository.getDiscoveryTracks(authToken)
                result.onSuccess { trackList ->
                    _tracks.value = trackList
                }.onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load discovery tracks"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load discovery tracks"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun handleTrackAction(
        track: Track,
        action: String,
        authToken: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val songAction = SongAction(
                isrc = track.isrc ?: "",
                songName = track.name,
                artist = track.artists.firstOrNull() ?: "",
                action = action,
                genres = track.genres
            )

            jamsyRepository.handleTrackAction(songAction, authToken).fold(
                onSuccess = {
                    if (action == "like") {
                        val currentLiked = _likedTracks.value.toMutableList()
                        currentLiked.add(track)
                        _likedTracks.value = currentLiked
                    }
                    nextTrack()
                    onComplete()
                },
                onFailure = {
                    // Still proceed to next track even if action fails
                    nextTrack()
                    onComplete()
                }
            )
        }
    }

    private fun nextTrack() {
        when (val currentState = _tracks.value) {
            is Resource.Success -> {
                val currentTracks = currentState.data
                if (_currentTrackIndex.value < currentTracks.size - 1) {
                    _currentTrackIndex.value += 1
                }
            }
            else -> { /* Do nothing for Loading or Error states */ }
        }
    }

    fun isLastTrack(): Boolean {
        return when (val currentState = _tracks.value) {
            is Resource.Success -> {
                val currentTracks = currentState.data
                _currentTrackIndex.value >= currentTracks.size - 1
            }
            else -> false
        }
    }

    fun getCurrentTrack(): Track? {
        return when (val currentState = _tracks.value) {
            is Resource.Success -> {
                val currentTracks = currentState.data
                if (_currentTrackIndex.value < currentTracks.size) {
                    currentTracks[_currentTrackIndex.value]
                } else null
            }
            else -> null
        }
    }

    fun clearData() {
        _tracks.value = emptyList()
        _currentTrackIndex.value = 0
        _likedTracks.value = emptyList()
    }
}