package ca.sheridancollege.jamsy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

import ca.sheridancollege.jamsy.domain.repository.TrackRepository
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.util.Resource

@HiltViewModel
class TrackListViewModel @Inject constructor(
    private val repository: TrackRepository
) : ViewModel() {

    private val _tracksState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val tracksState: StateFlow<Resource<List<Track>>> = _tracksState

    fun loadTracks() {
        viewModelScope.launch {
            _tracksState.value = Resource.Loading
            try {
                println("TrackListViewModel: Starting to load tracks...")
                val result = repository.getTracks()
                println("TrackListViewModel: Got result: $result")
                
                when (result) {
                    is Resource.Success -> {
                        val tracks = result.data
                        println("TrackListViewModel: Successfully loaded ${tracks.size} tracks")
                        _tracksState.value = Resource.Success(tracks)
                    }
                    is Resource.Error -> {
                        val error = result.message
                        println("TrackListViewModel: Error loading tracks: $error")
                        _tracksState.value = Resource.Error("Failed to load tracks: $error")
                    }
                    is Resource.Loading -> {
                        println("TrackListViewModel: Still loading...")
                        _tracksState.value = Resource.Loading
                    }
                }
            } catch (e: Exception) {
                println("TrackListViewModel: Exception loading tracks: ${e.message}")
                e.printStackTrace()
                _tracksState.value = Resource.Error("Failed to load tracks: ${e.message}")
            }
        }
    }

    fun clearData() {
        _tracksState.value = Resource.Loading
    }
}