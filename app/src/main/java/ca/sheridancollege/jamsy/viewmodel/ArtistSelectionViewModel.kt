package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.Artist
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.repository.JamsyRepository
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.data.DiscoveryDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArtistSelectionViewModel(
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
    private val _workout = MutableStateFlow<String>("")
    private val _mood = MutableStateFlow<String>("")

    fun loadArtists(workout: String, mood: String, authToken: String) {
        viewModelScope.launch {
            _artistsState.value = Resource.Loading
            _workout.value = workout
            _mood.value = mood
            
            try {
                val result = jamsyRepository.getArtistsByWorkout(workout, mood, authToken)
                result.onSuccess { artistList ->
                    _artistsState.value = Resource.Success(artistList)
                }.onFailure { exception ->
                    _artistsState.value = Resource.Error(exception.message ?: "Failed to load artists")
                }
            } catch (e: Exception) {
                _artistsState.value = Resource.Error(e.message ?: "Failed to load artists")
            }
        }
    }

    fun toggleArtistSelection(artist: Artist) {
        val currentSelection = _selectedArtists.value.toMutableList()
        if (currentSelection.contains(artist)) {
            currentSelection.remove(artist)
        } else {
            currentSelection.add(artist)
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
                    selectedArtistIds = selectedArtistIds,
                    artistNamesJson = artistNamesJson,
                    workout = workout,
                    mood = mood,
                    action = action,
                    authToken = authToken
                )
                
                result.onSuccess { tracks ->
                    // Store the discovery tracks in the data store
                    DiscoveryDataStore.setDiscoveryTracks(tracks)
                    DiscoveryDataStore.setWorkoutAndMood(workout, mood)
                    onSuccess()
                }.onFailure { exception ->
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