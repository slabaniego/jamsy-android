package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.repository.JamsyRepository
import ca.sheridancollege.jamsy.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: JamsyRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val searchState: StateFlow<Resource<List<Track>>> = _searchState.asStateFlow()

    fun searchTracks(
        query: String,
        excludeExplicit: Boolean = true,
        excludeLoveSongs: Boolean = false,
        excludeFolk: Boolean = false
    ) {
        viewModelScope.launch {
            _searchState.value = Resource.Loading
            try {
                // Get auth token from Firebase Auth or show error if not authenticated
                val authToken = getAuthToken()
                if (authToken == null) {
                    _searchState.value = Resource.Error("Please log in to search tracks")
                    return@launch
                }
                
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
    
    private fun getAuthToken(): String? {
        // TODO: Get actual auth token from Firebase Auth or AuthManager
        // For now, return null to show authentication is required
        return null
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