package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.Artist
import ca.sheridancollege.jamsy.repository.JamsyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArtistSelectionViewModel(
    private val jamsyRepository: JamsyRepository
) : ViewModel() {

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _selectedArtists = MutableStateFlow<List<String>>(emptyList())
    val selectedArtists: StateFlow<List<String>> = _selectedArtists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadArtists(workout: String, mood: String, authToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = jamsyRepository.getArtistsByWorkout(workout, mood, authToken)
                result.onSuccess { artistList ->
                    _artists.value = artistList
                }.onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load artists"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load artists"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleArtistSelection(artistId: String) {
        val currentSelection = _selectedArtists.value.toMutableList()
        if (currentSelection.contains(artistId)) {
            currentSelection.remove(artistId)
        } else {
            currentSelection.add(artistId)
        }
        _selectedArtists.value = currentSelection
    }
}