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
                println("DiscoveryViewModel: Starting to load discovery tracks...")
                
                // First try to get tracks from data store
                val dataStoreTracks = DiscoveryDataStore.discoveryTracks.value
                if (dataStoreTracks.isNotEmpty()) {
                    println("DiscoveryViewModel: Found ${dataStoreTracks.size} tracks in data store")
                    _tracksState.value = Resource.Success(dataStoreTracks)
                    _currentTrackIndex.value = 0
                    return@launch
                }

                // If no tracks in data store, try to load from API (without auth for basic discovery)
                println("DiscoveryViewModel: No tracks in data store, trying API...")
                val result = jamsyRepository.getDiscoveryTracks("") // Use empty token for basic discovery
                if (result.isSuccess) {
                    val tracks = result.getOrNull() ?: emptyList()
                    println("DiscoveryViewModel: Successfully loaded ${tracks.size} tracks from API")
                    
                    // Store tracks in data store for future use
                    DiscoveryDataStore.setDiscoveryTracks(tracks)
                    
                    _tracksState.value = Resource.Success(tracks)
                    _currentTrackIndex.value = 0
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    println("DiscoveryViewModel: Error loading tracks from API: $error")
                    _tracksState.value = Resource.Error("Failed to load discovery tracks: $error")
                }
            } catch (e: Exception) {
                println("DiscoveryViewModel: Exception loading discovery tracks: ${e.message}")
                e.printStackTrace()
                _tracksState.value = Resource.Error("Failed to load discovery tracks: ${e.message}")
            }
        }
    }

    // Load discovery tracks without authentication (for basic discovery)
    fun loadBasicDiscoveryTracks() {
        viewModelScope.launch {
            _tracksState.value = Resource.Loading

            try {
                println("DiscoveryViewModel: Starting to load basic discovery tracks...")
                
                // First try to get tracks from data store
                val dataStoreTracks = DiscoveryDataStore.discoveryTracks.value
                if (dataStoreTracks.isNotEmpty()) {
                    println("DiscoveryViewModel: Found ${dataStoreTracks.size} tracks in data store")
                    _tracksState.value = Resource.Success(dataStoreTracks)
                    _currentTrackIndex.value = 0
                    return@launch
                }

                // Load from API without authentication
                println("DiscoveryViewModel: Loading from API without authentication...")
                val result = jamsyRepository.getDiscoveryTracks("")
                if (result.isSuccess) {
                    val tracks = result.getOrNull() ?: emptyList()
                    println("DiscoveryViewModel: Successfully loaded ${tracks.size} tracks from API")
                    
                    // Store tracks in data store for future use
                    DiscoveryDataStore.setDiscoveryTracks(tracks)
                    
                    _tracksState.value = Resource.Success(tracks)
                    _currentTrackIndex.value = 0
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    println("DiscoveryViewModel: Error loading tracks from API: $error")
                    _tracksState.value = Resource.Error("Failed to load discovery tracks: $error")
                }
            } catch (e: Exception) {
                println("DiscoveryViewModel: Exception loading discovery tracks: ${e.message}")
                e.printStackTrace()
                _tracksState.value = Resource.Error("Failed to load discovery tracks: ${e.message}")
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
                genres = track.genres ?: emptyList()
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