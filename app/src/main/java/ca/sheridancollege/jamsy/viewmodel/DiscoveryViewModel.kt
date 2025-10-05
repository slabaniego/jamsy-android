package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.SongAction
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.repository.JamsyRepository
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.data.DiscoveryDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val jamsyRepository: JamsyRepository
) : ViewModel() {

    private val instanceId = System.currentTimeMillis() // Unique identifier for this instance
    
    private val _tracksState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val tracksState: StateFlow<Resource<List<Track>>> = _tracksState.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _likedTracks = MutableStateFlow<List<Track>>(emptyList())
    val likedTracks: StateFlow<List<Track>> = _likedTracks.asStateFlow()
    
    init {
        println("DiscoveryViewModel: ===== NEW INSTANCE CREATED (ID: $instanceId) =====")
        println("DiscoveryViewModel: Initial _currentTrackIndex: ${_currentTrackIndex.value}")
        println("DiscoveryViewModel: Initial _tracksState: ${_tracksState.value}")
        println("DiscoveryViewModel: Stack trace for instance creation:")
        Thread.currentThread().stackTrace.take(10).forEach { 
            println("DiscoveryViewModel:   at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})")
        }
        
        // Force reset the index to 0 when ViewModel is created
        println("DiscoveryViewModel[$instanceId]: Forcing _currentTrackIndex to 0 in init")
        _currentTrackIndex.value = 0
        println("DiscoveryViewModel[$instanceId]: After init reset - _currentTrackIndex: ${_currentTrackIndex.value}")
    }

    fun loadDiscoveryTracks(authToken: String) {
        viewModelScope.launch {
            println("DiscoveryViewModel[$instanceId]: ===== DISCOVERY SESSION STARTED =====")
            println("DiscoveryViewModel[$instanceId]: loadDiscoveryTracks called with authToken length: ${authToken.length}")
            println("DiscoveryViewModel[$instanceId]: Current _currentTrackIndex before loading: ${_currentTrackIndex.value}")
            _tracksState.value = Resource.Loading

            try {
                println("DiscoveryViewModel[$instanceId]: Starting to load discovery tracks...")
                
                // First try to get tracks from data store
                val dataStoreTracks = DiscoveryDataStore.discoveryTracks.value
                println("DiscoveryViewModel[$instanceId]: Data store tracks count: ${dataStoreTracks.size}")
                println("DiscoveryViewModel[$instanceId]: Data store tracks: ${dataStoreTracks.map { "${it.name} by ${it.artists.firstOrNull()}" }}")
                
                if (dataStoreTracks.isNotEmpty()) {
                    println("DiscoveryViewModel[$instanceId]: Found ${dataStoreTracks.size} tracks in data store - using them!")
                    println("DiscoveryViewModel[$instanceId]: BEFORE setting tracks, current index is: ${_currentTrackIndex.value}")
                    _tracksState.value = Resource.Success(dataStoreTracks)
                    // Only reset index if it's out of bounds or if this is a new session
                    if (_currentTrackIndex.value >= dataStoreTracks.size) {
                        _currentTrackIndex.value = 0
                        println("DiscoveryViewModel[$instanceId]: Index was out of bounds, reset to 0")
                    } else {
                        println("DiscoveryViewModel[$instanceId]: Preserving current index: ${_currentTrackIndex.value}")
                    }
                    println("DiscoveryViewModel[$instanceId]: ✅ SUCCESS! Tracks loaded from data store")
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
                    // Only reset index if it's out of bounds
                    if (_currentTrackIndex.value >= dataStoreTracks.size) {
                        _currentTrackIndex.value = 0
                        println("DiscoveryViewModel: Index was out of bounds, reset to 0")
                    } else {
                        println("DiscoveryViewModel: Preserving current index: ${_currentTrackIndex.value}")
                    }
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
                    _currentTrackIndex.value = 0
                    println("DiscoveryViewModel: Set currentTrackIndex to 0 (artist selection)")
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
        println("DiscoveryViewModel: ===== handleTrackAction CALLED =====")
        println("DiscoveryViewModel: Track: ${track.name} by ${track.artists.firstOrNull()}")
        println("DiscoveryViewModel: Action: $action")
        println("DiscoveryViewModel: AuthToken length: ${authToken.length}")
        println("DiscoveryViewModel: Current liked tracks count: ${_likedTracks.value.size}")
        
        viewModelScope.launch {
            println("DiscoveryViewModel: AuthToken isBlank: ${authToken.isBlank()}")
            
            // Check if auth token is provided
            if (authToken.isBlank()) {
                println("DiscoveryViewModel: No auth token provided, proceeding without API call")
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
            
            println("DiscoveryViewModel: Created SongAction - ISRC: ${songAction.isrc}, Song: ${songAction.songName}, Artist: ${songAction.artist}, Action: ${songAction.action}")
            println("DiscoveryViewModel: Calling jamsyRepository.handleTrackAction...")

            jamsyRepository.handleTrackAction(songAction, authToken).fold(
                onSuccess = { result ->
                    println("DiscoveryViewModel: ===== Track action successful! =====")
                    println("DiscoveryViewModel: Result: $result")
                    println("DiscoveryViewModel: Action: $action")
                    if (action == "like") {
                        println("DiscoveryViewModel: Processing like action...")
                        val currentLiked = _likedTracks.value.toMutableList()
                        println("DiscoveryViewModel: Current liked tracks before adding: ${currentLiked.size}")
                        // Check if track is already liked to prevent duplicates
                        val isAlreadyLiked = currentLiked.any { likedTrack -> 
                            likedTrack.name == track.name && 
                            likedTrack.artists.firstOrNull() == track.artists.firstOrNull()
                        }
                        println("DiscoveryViewModel: Is track already liked: $isAlreadyLiked")
                        if (!isAlreadyLiked) {
                            currentLiked.add(track)
                            _likedTracks.value = currentLiked
                            println("DiscoveryViewModel: ✅ Added track to liked list!")
                            println("DiscoveryViewModel: Total liked tracks after adding: ${_likedTracks.value.size}")
                            println("DiscoveryViewModel: Liked tracks: ${_likedTracks.value.map { "${it.name} by ${it.artists.firstOrNull()}" }}")
                        } else {
                            println("DiscoveryViewModel: Track already liked, skipping duplicate")
                        }
                    } else {
                        println("DiscoveryViewModel: Not a like action, skipping liked tracks update")
                    }
                    println("DiscoveryViewModel: Calling nextTrack()...")
                    nextTrack()
                    println("DiscoveryViewModel: Calling onComplete()...")
                    onComplete()
                },
                onFailure = { exception ->
                    println("DiscoveryViewModel: Failed to handle track action: ${exception.message}")
                    println("DiscoveryViewModel: Exception type: ${exception.javaClass.simpleName}")
                    exception.printStackTrace()
                    // Still proceed to next track even if action fails
                    nextTrack()
                    onComplete()
                }
            )
        }
    }

    private fun nextTrack() {
        println("DiscoveryViewModel: ===== nextTrack() called =====")
        when (val currentState = _tracksState.value) {
            is Resource.Success -> {
                val currentTracks = currentState.data
                val currentIndex = _currentTrackIndex.value
                val totalTracks = currentTracks.size
                
                println("DiscoveryViewModel: nextTrack called - Current index: $currentIndex, Total tracks: $totalTracks")
                
                if (currentIndex < totalTracks - 1) {
                    println("DiscoveryViewModel: BEFORE incrementing - _currentTrackIndex.value: ${_currentTrackIndex.value}")
                    _currentTrackIndex.value += 1
                    val newIndex = _currentTrackIndex.value
                    println("DiscoveryViewModel: AFTER incrementing - _currentTrackIndex.value: ${_currentTrackIndex.value}")
                    println("DiscoveryViewModel: Moved to next track - New index: $newIndex")
                    
                    // Check if this is the last track
                    if (newIndex >= totalTracks - 1) {
                        println("DiscoveryViewModel: Reached last track! Discovery session complete.")
                        println("DiscoveryViewModel: Total liked tracks: ${_likedTracks.value.size}")
                        println("DiscoveryViewModel: Liked tracks: ${_likedTracks.value.map { "${it.name} by ${it.artists.firstOrNull()}" }}")
                    }
                } else {
                    println("DiscoveryViewModel: Already at last track, cannot move forward")
                }
            }
            else -> { 
                println("DiscoveryViewModel: nextTrack called but tracks not loaded yet")
            }
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
        val currentState = _tracksState.value
        val currentIndex = _currentTrackIndex.value
        
        println("DiscoveryViewModel[$instanceId]: getCurrentTrack called")
        println("DiscoveryViewModel[$instanceId]: Current state: $currentState")
        println("DiscoveryViewModel[$instanceId]: Current index: $currentIndex")
        println("DiscoveryViewModel[$instanceId]: _currentTrackIndex.value directly: ${_currentTrackIndex.value}")
        
        return when (currentState) {
            is Resource.Success -> {
                val currentTracks = currentState.data
                println("DiscoveryViewModel[$instanceId]: Total tracks: ${currentTracks.size}")
                
                if (currentIndex < currentTracks.size) {
                    val track = currentTracks[currentIndex]
                    println("DiscoveryViewModel[$instanceId]: Returning track at index $currentIndex: ${track.name} by ${track.artists.firstOrNull()}")
                    track
                } else {
                    println("DiscoveryViewModel[$instanceId]: Index $currentIndex is out of bounds for ${currentTracks.size} tracks")
                    null
                }
            }
            else -> {
                println("DiscoveryViewModel[$instanceId]: Tracks not loaded yet or error state")
                null
            }
        }
    }

    fun clearData() {
        _tracksState.value = Resource.Loading
        _currentTrackIndex.value = 0
        _likedTracks.value = emptyList()
    }
    
    fun clearLikedTracks() {
        println("DiscoveryViewModel[$instanceId]: clearLikedTracks called")
        _likedTracks.value = emptyList()
    }
    
    fun startNewDiscoverySession() {
        println("DiscoveryViewModel[$instanceId]: startNewDiscoverySession called - clearing liked tracks for new session")
        _likedTracks.value = emptyList()
        _currentTrackIndex.value = 0
        _tracksState.value = Resource.Loading
    }
    
    fun resetToFirstTrack() {
        println("DiscoveryViewModel[$instanceId]: resetToFirstTrack called")
        println("DiscoveryViewModel[$instanceId]: BEFORE reset - _currentTrackIndex.value: ${_currentTrackIndex.value}")
        _currentTrackIndex.value = 0
        println("DiscoveryViewModel[$instanceId]: AFTER reset - _currentTrackIndex.value: ${_currentTrackIndex.value}")
    }
    
    fun resetViewModel(clearLikedTracks: Boolean = false) {
        println("DiscoveryViewModel[$instanceId]: resetViewModel called (clearLikedTracks: $clearLikedTracks)")
        println("DiscoveryViewModel[$instanceId]: BEFORE reset - _currentTrackIndex.value: ${_currentTrackIndex.value}")
        println("DiscoveryViewModel[$instanceId]: BEFORE reset - _tracksState.value: ${_tracksState.value}")
        println("DiscoveryViewModel[$instanceId]: BEFORE reset - likedTracks count: ${_likedTracks.value.size}")
        
        // Only reset index if we're starting a new session
        if (clearLikedTracks) {
            _currentTrackIndex.value = 0
            _likedTracks.value = emptyList()
            _tracksState.value = Resource.Loading
            println("DiscoveryViewModel[$instanceId]: Cleared liked tracks and reset index for new session")
        } else {
            // For existing sessions, preserve the current state
            println("DiscoveryViewModel[$instanceId]: Preserving current state for existing session")
        }
        
        println("DiscoveryViewModel[$instanceId]: AFTER reset - _currentTrackIndex.value: ${_currentTrackIndex.value}")
        println("DiscoveryViewModel[$instanceId]: AFTER reset - _tracksState.value: ${_tracksState.value}")
        println("DiscoveryViewModel[$instanceId]: AFTER reset - likedTracks count: ${_likedTracks.value.size}")
    }
    
    fun forceResetIndex() {
        println("DiscoveryViewModel[$instanceId]: forceResetIndex called - setting index to 0")
        _currentTrackIndex.value = 0
    }
}