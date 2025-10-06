package ca.sheridancollege.jamsy.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import ca.sheridancollege.jamsy.domain.models.Track

/**
 * Simple data store for sharing discovery tracks between ViewModels
 */
object DiscoveryDataStore {
    
    private val _discoveryTracks = MutableStateFlow<List<Track>>(emptyList())
    val discoveryTracks: StateFlow<List<Track>> = _discoveryTracks.asStateFlow()
    
    private val _workout = MutableStateFlow<String>("")
    val workout: StateFlow<String> = _workout.asStateFlow()
    
    private val _mood = MutableStateFlow<String>("")
    val mood: StateFlow<String> = _mood.asStateFlow()
    
    private val _isNewSession = MutableStateFlow<Boolean>(false)
    val isNewSession: StateFlow<Boolean> = _isNewSession.asStateFlow()
    
    private val _likedTracks = MutableStateFlow<List<Track>>(emptyList())
    val likedTracks: StateFlow<List<Track>> = _likedTracks.asStateFlow()
    
    fun setDiscoveryTracks(tracks: List<Track>) {
        println("DiscoveryDataStore: Setting ${tracks.size} tracks")
        println("DiscoveryDataStore: Track names: ${tracks.map { "${it.name} by ${it.artists.firstOrNull()}" }}")
        _discoveryTracks.value = tracks
        _isNewSession.value = true // Mark as new session when tracks are set
        println("DiscoveryDataStore: Tracks stored successfully. Current count: ${_discoveryTracks.value.size}")
        println("DiscoveryDataStore: New session flag set to true")
    }
    
    fun setWorkoutAndMood(workout: String, mood: String) {
        _workout.value = workout
        _mood.value = mood
    }
    
    fun getCurrentTracks(): List<Track> {
        val tracks = _discoveryTracks.value
        println("DiscoveryDataStore: getCurrentTracks() called - returning ${tracks.size} tracks")
        return tracks
    }
    
    fun setLikedTracks(tracks: List<Track>) {
        println("DiscoveryDataStore: Setting ${tracks.size} liked tracks")
        _likedTracks.value = tracks
    }
    
    fun addLikedTrack(track: Track) {
        val current = _likedTracks.value.toMutableList()
        if (!current.any { it.name == track.name && it.artists.firstOrNull() == track.artists.firstOrNull() }) {
            current.add(track)
            _likedTracks.value = current
            println("DiscoveryDataStore: Added liked track, total: ${current.size}")
        }
    }
    
    fun clear() {
        println("DiscoveryDataStore: Clearing all data")
        _discoveryTracks.value = emptyList()
        _workout.value = ""
        _mood.value = ""
        _isNewSession.value = false
        _likedTracks.value = emptyList()
    }
    
    fun markSessionAsStarted() {
        println("DiscoveryDataStore: Marking session as started (no longer new)")
        _isNewSession.value = false
    }
}
