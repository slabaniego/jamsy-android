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
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.domain.usecases.GetDiscoveryTracksUseCase
import ca.sheridancollege.jamsy.domain.usecases.HandleTrackActionUseCase
import ca.sheridancollege.jamsy.util.Resource

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val getDiscoveryTracksUseCase: GetDiscoveryTracksUseCase,
    private val handleTrackActionUseCase: HandleTrackActionUseCase,
) : ViewModel() {

    private val _tracksState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val tracksState: StateFlow<Resource<List<Track>>> = _tracksState.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _likedTracks = MutableStateFlow<List<Track>>(emptyList())
    val likedTracks: StateFlow<List<Track>> = _likedTracks.asStateFlow()
    
    init {
        // Ensure we start from the first track when the ViewModel is created
        _currentTrackIndex.value = 0
    }

    fun loadDiscoveryTracks(authToken: String) {
        viewModelScope.launch {
            _tracksState.value = Resource.Loading

            try {
                // First try to get tracks from data store
                val dataStoreTracks = DiscoveryDataStore.discoveryTracks.value
                
                if (dataStoreTracks.isNotEmpty()) {
                    _tracksState.value = Resource.Success(dataStoreTracks)
                    // Only reset index if it's out of bounds or if this is a new session
                    if (_currentTrackIndex.value >= dataStoreTracks.size) {
                        _currentTrackIndex.value = 0
                    }
                    return@launch
                }

                // If no tracks in data store, try to load from API using use case
                val result = getDiscoveryTracksUseCase.getBasicDiscoveryTracks()
                if (result is Resource.Success) {
                    val tracks = result.data
                    // Store tracks in data store for future use
                    DiscoveryDataStore.setDiscoveryTracks(tracks)
                    
                    _tracksState.value = Resource.Success(tracks)
                    _currentTrackIndex.value = 0
                } else {
                    val error = (result as? Resource.Error)?.message ?: "Unknown error"
                    _tracksState.value = Resource.Error("Failed to load discovery tracks: $error")
                }
            } catch (e: Exception) {
                _tracksState.value = Resource.Error("Failed to load discovery tracks: ${e.message}")
            }
        }
    }

    // Load discovery tracks without authentication (for basic discovery)
    fun loadBasicDiscoveryTracks() {
        viewModelScope.launch {
            _tracksState.value = Resource.Loading

            try {
                // First try to get tracks from data store
                val dataStoreTracks = DiscoveryDataStore.discoveryTracks.value
                if (dataStoreTracks.isNotEmpty()) {
                    _tracksState.value = Resource.Success(dataStoreTracks)
                    // Only reset index if it's out of bounds
                    if (_currentTrackIndex.value >= dataStoreTracks.size) {
                        _currentTrackIndex.value = 0
                    }
                    return@launch
                }

                // Load from API without authentication using use case
                val result = getDiscoveryTracksUseCase.getBasicDiscoveryTracks()
                if (result is Resource.Success) {
                    val tracks = result.data
                    // Store tracks in data store for future use
                    DiscoveryDataStore.setDiscoveryTracks(tracks)
                    
                    _tracksState.value = Resource.Success(tracks)
                    _currentTrackIndex.value = 0
                } else {
                    val error = (result as? Resource.Error)?.message ?: "Unknown error"
                    _tracksState.value = Resource.Error("Failed to load discovery tracks: $error")
                }
            } catch (e: Exception) {
                _tracksState.value = Resource.Error("Failed to load discovery tracks: ${e.message}")
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
            val result = handleTrackActionUseCase(track, action, authToken)
            when (result) {
                is Resource.Success -> {
                    if (action == "like") {
                        val currentLiked = _likedTracks.value.toMutableList()
                        // Check if track is already liked to prevent duplicates
                        val isAlreadyLiked = currentLiked.any { likedTrack -> 
                            likedTrack.name == track.name && 
                            likedTrack.artists.firstOrNull() == track.artists.firstOrNull()
                        }
                        if (!isAlreadyLiked) {
                            currentLiked.add(track)
                            _likedTracks.value = currentLiked
                            // Also store in DiscoveryDataStore so GeneratedPlaylistViewModel can access it
                            DiscoveryDataStore.addLikedTrack(track)
                        }
                    }
                    nextTrack()
                    onComplete()
                }
                is Resource.Error -> {
                    // Still proceed to next track even if action fails
                    nextTrack()
                    onComplete()
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun nextTrack() {
        when (val currentState = _tracksState.value) {
            is Resource.Success -> {
                val currentTracks = currentState.data
                val currentIndex = _currentTrackIndex.value
                val totalTracks = currentTracks.size

                if (currentIndex < totalTracks - 1) {
                    _currentTrackIndex.value += 1
                    val newIndex = _currentTrackIndex.value
                    // Check if this is the last track
                    if (newIndex >= totalTracks - 1) {
                        // Reached last track in current session
                        Unit
                    }
                }
            }
            else -> Unit
        }
    }

    fun getCurrentTrack(): Track? {
        val currentState = _tracksState.value
        val currentIndex = _currentTrackIndex.value

        return when (currentState) {
            is Resource.Success -> {
                val currentTracks = currentState.data
                if (currentIndex < currentTracks.size) {
                    currentTracks[currentIndex]
                } else {
                    null
                }
            }
            else -> null
        }
    }

    fun startNewDiscoverySession() {
        _likedTracks.value = emptyList()
        _currentTrackIndex.value = 0
        _tracksState.value = Resource.Loading
    }
}