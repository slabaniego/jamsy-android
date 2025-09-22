package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.SongAction
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.repository.JamsyRepository
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.data.DiscoveryDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiscoveryViewModel(
    private val jamsyRepository: JamsyRepository
) : ViewModel() {

    private val _tracksState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val tracksState: StateFlow<Resource<List<Track>>> = _tracksState.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _likedTracks = MutableStateFlow<List<Track>>(emptyList())
    val likedTracks: StateFlow<List<Track>> = _likedTracks.asStateFlow()

    fun loadDiscoveryTracks(authToken: String) {
        viewModelScope.launch {
            _tracksState.value = Resource.Loading

            try {
                // Check if auth token is provided
                if (authToken.isBlank()) {
                    _tracksState.value = Resource.Error("Please log in to discover tracks")
                    return@launch
                }

                // Get tracks from data store
                val tracks = DiscoveryDataStore.discoveryTracks.value
                if (tracks.isNotEmpty()) {
                    _tracksState.value = Resource.Success(tracks)
                    _currentTrackIndex.value = 0
                } else {
                    _tracksState.value = Resource.Error("Please select artists first to discover tracks")
                }
            } catch (e: Exception) {
                _tracksState.value = Resource.Error(e.message ?: "Failed to load discovery tracks")
            }
        }
    }

    // New method to get discovery tracks through artist selection
    fun getDiscoveryTracksFromArtists(
        selectedArtistIds: List<String>,
        artistNames: List<String>,
        workout: String,
        mood: String,
        authToken: String
    ) {
        viewModelScope.launch {
            _tracksState.value = Resource.Loading

            try {
                if (authToken.isBlank()) {
                    _tracksState.value = Resource.Error("Please log in to discover tracks")
                    return@launch
                }

                val artistNamesJson = artistNames.joinToString(",")
                val result = jamsyRepository.submitArtistSelection(
                    selectedArtistIds = selectedArtistIds,
                    artistNamesJson = artistNamesJson,
                    workout = workout,
                    mood = mood,
                    action = "discover",
                    authToken = authToken
                )

                result.onSuccess { trackList ->
                    _tracksState.value = Resource.Success(trackList)
                }.onFailure { exception ->
                    _tracksState.value = Resource.Error(exception.message ?: "Failed to get discovery tracks")
                }
            } catch (e: Exception) {
                _tracksState.value = Resource.Error(e.message ?: "Failed to get discovery tracks")
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
            // Check if auth token is provided
            if (authToken.isBlank()) {
                // Still proceed to next track even if not authenticated
                nextTrack()
                onComplete()
                return@launch
            }

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
        when (val currentState = _tracksState.value) {
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
        return when (val currentState = _tracksState.value) {
            is Resource.Success -> {
                val currentTracks = currentState.data
                _currentTrackIndex.value >= currentTracks.size - 1
            }
            else -> false
        }
    }

    fun getCurrentTrack(): Track? {
        return when (val currentState = _tracksState.value) {
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
        _tracksState.value = Resource.Loading
        _currentTrackIndex.value = 0
        _likedTracks.value = emptyList()
    }
}