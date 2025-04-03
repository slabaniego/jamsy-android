package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.repository.TrackRepository
import ca.sheridancollege.jamsy.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrackListViewModel(private val repository: TrackRepository) : ViewModel() {

    private val _tracksState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val tracksState: StateFlow<Resource<List<Track>>> = _tracksState

    fun loadTracks() {
        viewModelScope.launch {
            _tracksState.value = Resource.Loading
            try {
                val result = repository.getTracks()
                _tracksState.value = result
            } catch (e: Exception) {
                _tracksState.value = Resource.Error("Failed to load tracks: ${e.message}")
            }
        }
    }

    fun clearData() {
        _tracksState.value = Resource.Loading
    }
}