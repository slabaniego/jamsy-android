package ca.sheridancollege.jamsy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.model.TrackActionRequest
import ca.sheridancollege.jamsy.repository.JamsyRepository
import kotlinx.coroutines.launch

class TracksViewModel : ViewModel() {

    private val repository = JamsyRepository()

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private val _actionStatus = MutableLiveData<String>()
    val actionStatus: LiveData<String> = _actionStatus

    fun loadTracks(
        excludeExplicit: Boolean = true,
        excludeLoveSongs: Boolean = false,
        excludeFolk: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.getTracks(excludeExplicit, excludeLoveSongs, excludeFolk)
                .onSuccess { tracksList ->
                    _tracks.value = tracksList
                    _isLoading.value = false
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Failed to load tracks"
                    _isLoading.value = false
                }
        }
    }

    fun searchTracks(
        query: String,
        accessToken: String,
        excludeExplicit: Boolean = true,
        excludeLoveSongs: Boolean = false,
        excludeFolk: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.searchTracks(query, accessToken, excludeExplicit, excludeLoveSongs, excludeFolk)
                .onSuccess { tracksList ->
                    _tracks.value = tracksList
                    _isLoading.value = false
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Failed to search tracks"
                    _isLoading.value = false
                }
        }
    }

    fun trackAction(
        track: Track,
        action: String
    ) {
        viewModelScope.launch {

            val request = TrackActionRequest(
                isrc = track.isrc,
                songName = track.name,
                artist = track.artists,
                genres = track.genres?.joinToString(","),
                action = action
            )

            repository.trackAction(request)
                .onSuccess { message ->
                    _actionStatus.value = message
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Failed to perform action"
                }
        }
    }

    fun getSimilarTracks(
        track: Track,
        excludeExplicit: Boolean = true,
        excludeLoveSongs: Boolean = false,
        excludeFolk: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.getSimilarTracks(
                track.name,
                track.artists,
                excludeExplicit,
                excludeLoveSongs,
                excludeFolk
            )
                .onSuccess { tracksList ->
                    _tracks.value = tracksList
                    _isLoading.value = false
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Failed to get similar tracks"
                    _isLoading.value = false
                }
        }
    }
}