/*
 * LikedTracksViewModel.kt
 * ViewModel for loading and exposing the user's liked tracks.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import ca.sheridancollege.jamsy.data.repository.TrackRepository
import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.util.Resource

@HiltViewModel
class LikedTracksViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
) : ViewModel() {

    private val _likedTracksState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)

    fun loadLikedTracks(authToken: String) {
        viewModelScope.launch {
            _likedTracksState.value = Resource.Loading
            _likedTracksState.value = trackRepository.getLikedTracks(authToken)
        }
    }
}