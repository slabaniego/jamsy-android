package ca.sheridancollege.jamsy.data

import ca.sheridancollege.jamsy.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    
    fun setDiscoveryTracks(tracks: List<Track>) {
        _discoveryTracks.value = tracks
    }
    
    fun setWorkoutAndMood(workout: String, mood: String) {
        _workout.value = workout
        _mood.value = mood
    }
    
    fun clear() {
        _discoveryTracks.value = emptyList()
        _workout.value = ""
        _mood.value = ""
    }
}
