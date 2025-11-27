/*
 * DiscoveryDataStore.kt
 * Shared in-memory store for discovery tracks, workout, mood, and liked tracks.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import ca.sheridancollege.jamsy.domain.models.Track

object DiscoveryDataStore {
    private val _discoveryTracks = MutableStateFlow<List<Track>>(emptyList())
    val discoveryTracks: StateFlow<List<Track>> = _discoveryTracks.asStateFlow()
    
    private val _workout = MutableStateFlow("")
    val workout: StateFlow<String> = _workout.asStateFlow()
    private val _mood = MutableStateFlow("")
    
    private val _isNewSession = MutableStateFlow(false)
    val isNewSession: StateFlow<Boolean> = _isNewSession.asStateFlow()
    
    private val _likedTracks = MutableStateFlow<List<Track>>(emptyList())
    val likedTracks: StateFlow<List<Track>> = _likedTracks.asStateFlow()
    
    fun setDiscoveryTracks(tracks: List<Track>) {
        _discoveryTracks.value = tracks
        _isNewSession.value = true // Mark as new session when tracks are set
    }
    
    fun setWorkoutAndMood(workout: String, mood: String) {
        _workout.value = workout
        _mood.value = mood
    }
    
    fun addLikedTrack(track: Track) {
        val current = _likedTracks.value.toMutableList()
        if (!current.any { it.name == track.name && it.artists.firstOrNull() == track.artists.firstOrNull() }) {
            current.add(track)
            _likedTracks.value = current
        }
    }
    fun clear() {
        _discoveryTracks.value = emptyList()
        _workout.value = ""
        _mood.value = ""
        _isNewSession.value = false
        _likedTracks.value = emptyList()
    }
    
    fun markSessionAsStarted() {
        _isNewSession.value = false
    }
}
