package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.repository.JamsyRepository
import ca.sheridancollege.jamsy.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: JamsyRepository = ca.sheridancollege.jamsy.di.NetworkModule.jamsyRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val searchState: StateFlow<Resource<List<Track>>> = _searchState.asStateFlow()

    fun searchTracks(
        query: String,
        authToken: String,
        excludeExplicit: Boolean = true,
        excludeLoveSongs: Boolean = false,
        excludeFolk: Boolean = false
    ) {
        viewModelScope.launch {
            _searchState.value = Resource.Loading
            try {
                val result = repository.searchTracks(
                    query = query,
                    authToken = authToken,
                    excludeExplicit = excludeExplicit,
                    excludeLoveSongs = excludeLoveSongs,
                    excludeFolk = excludeFolk
                )
                _searchState.value = if (result.isSuccess) {
                    Resource.Success(result.getOrNull() ?: emptyList())
                } else {
                    Resource.Error(result.exceptionOrNull()?.message ?: "Failed to search tracks")
                }
            } catch (e: Exception) {
                _searchState.value = Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun clearSearchResults() {
        _searchState.value = Resource.Success(emptyList())
    }

    fun handleTrackAction(track: Track, action: String) {
        viewModelScope.launch {
            // TODO: Implement track action handling (like/dislike)
            // This would call the backend API to record the user's action
        }
    }
}