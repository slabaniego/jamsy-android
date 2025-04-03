package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.repository.TrackRepository
import ca.sheridancollege.jamsy.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val trackRepository: TrackRepository) : ViewModel() {

    private val _tracksState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val tracksState: StateFlow<Resource<List<Track>>> = _tracksState

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex

    init {
        fetchTracksFromBackend()
    }

    fun fetchTracksFromBackend() {
        _tracksState.value = Resource.Loading
        viewModelScope.launch {
            val result = trackRepository.getTracks()
            _tracksState.value = result
        }
    }

    fun likeCurrentTrack() {
        val currentTracks = (_tracksState.value as? Resource.Success)?.data ?: return
        val currentIndex = _currentTrackIndex.value
        if (currentIndex < currentTracks.size) {
            viewModelScope.launch {
                val track = currentTracks[currentIndex]
                trackRepository.likeTrack(track)
                moveToNextTrack()
            }
        }
    }

    fun unlikeCurrentTrack() {
        val currentTracks = (_tracksState.value as? Resource.Success)?.data ?: return
        val currentIndex = _currentTrackIndex.value
        if (currentIndex < currentTracks.size) {
            viewModelScope.launch {
                val track = currentTracks[currentIndex]
                trackRepository.unlikeTrack(track)
                moveToNextTrack()
            }
        }
    }

    fun moveToNextTrack() {
        val currentTracks = (_tracksState.value as? Resource.Success)?.data ?: return
        val newIndex = _currentTrackIndex.value + 1
        if (newIndex < currentTracks.size) {
            _currentTrackIndex.value = newIndex
        }
    }

    fun clearData() {
        _tracksState.value = Resource.Loading
        _currentTrackIndex.value = 0
    }
}