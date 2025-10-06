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
import ca.sheridancollege.jamsy.data.repository.JamsyRepository
import ca.sheridancollege.jamsy.domain.models.Artist
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.util.Resource

@HiltViewModel
class ArtistSelectionViewModel @Inject constructor(
    private val jamsyRepository: JamsyRepository
) : ViewModel() {

    private val _artistsState = MutableStateFlow<Resource<List<Artist>>>(Resource.Loading)
    val artistsState: StateFlow<Resource<List<Artist>>> = _artistsState.asStateFlow()

    private val _selectedArtists = MutableStateFlow<List<Artist>>(emptyList())
    val selectedArtists: StateFlow<List<Artist>> = _selectedArtists.asStateFlow()
    
    // Store discovery tracks from artist selection
    private val _discoveryTracks = MutableStateFlow<List<Track>>(emptyList())
    val discoveryTracks: StateFlow<List<Track>> = _discoveryTracks.asStateFlow()
    
    // Store workout and mood for discovery
    private val _workout = MutableStateFlow("")
    private val _mood = MutableStateFlow("")

    fun loadArtists(workout: String, mood: String, authToken: String) {
        viewModelScope.launch {
            _artistsState.value = Resource.Loading
            _workout.value = workout
            _mood.value = mood
            
            try {
                println("ArtistSelectionViewModel: Loading artists for workout: $workout, mood: $mood, authToken: ${if (authToken.isBlank()) "EMPTY" else "PRESENT"}")
                val result = jamsyRepository.getArtistsByWorkout(workout, mood, authToken)
                result.onSuccess { artistList ->
                    println("ArtistSelectionViewModel: Successfully loaded ${artistList.size} artists")
                    _artistsState.value = Resource.Success(artistList)
                }.onFailure { exception ->
                    println("ArtistSelectionViewModel: Error loading artists: ${exception.message}")
                    _artistsState.value = Resource.Error(exception.message ?: "Failed to load artists")
                }
            } catch (e: Exception) {
                println("ArtistSelectionViewModel: Exception loading artists: ${e.message}")
                _artistsState.value = Resource.Error(e.message ?: "Failed to load artists")
            }
        }
    }

    fun setErrorState(message: String) {
        _artistsState.value = Resource.Error(message)
    }

    fun toggleArtistSelection(artist: Artist) {
        val currentSelection = _selectedArtists.value.toMutableList()
        if (currentSelection.contains(artist)) {
            currentSelection.remove(artist)
        } else {
            // Only allow selection if less than 5 artists are selected
            if (currentSelection.size < 5) {
                currentSelection.add(artist)
            }
        }
        _selectedArtists.value = currentSelection
    }

    fun submitSelection(
        workout: String,
        mood: String,
        action: String,
        authToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val selectedArtistIds = _selectedArtists.value.mapNotNull { it.id }
                val artistNamesJson = _selectedArtists.value.map { it.name }.joinToString(",")
                
                val result = jamsyRepository.submitArtistSelection(
                    _selectedArtistIds = selectedArtistIds,
                    artistNamesJson = artistNamesJson,
                    workout = workout,
                    _mood = mood,
                    _action = action,
                    authToken = authToken
                )
                
                result.onSuccess { tracks ->
                    println("ArtistSelectionViewModel: Successfully received ${tracks.size} tracks from API")
                    println("ArtistSelectionViewModel: Tracks: ${tracks.map { "${it.name} by ${it.artists.firstOrNull()}" }}")
                    
                    // Store the discovery tracks in the data store
                    DiscoveryDataStore.setDiscoveryTracks(tracks)
                    DiscoveryDataStore.setWorkoutAndMood(workout, mood)
                    
                    println("ArtistSelectionViewModel: Stored tracks in DiscoveryDataStore")
                    println("ArtistSelectionViewModel: Data store now contains: ${DiscoveryDataStore.discoveryTracks.value.size} tracks")
                    
                    onSuccess()
                }.onFailure { exception ->
                    println("ArtistSelectionViewModel: Error submitting selection: ${exception.message}")
                    onError(exception.message ?: "Failed to submit selection")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to submit selection")
            }
        }
    }
    
    // Get discovery tracks for the discovery screen
    fun getDiscoveryTracks(): List<Track> {
        return _discoveryTracks.value
    }
    
    // Get workout and mood for discovery context
    fun getWorkout(): String = _workout.value
    fun getMood(): String = _mood.value
}