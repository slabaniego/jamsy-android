package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import ca.sheridancollege.jamsy.model.Track
import ca.sheridancollege.jamsy.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {

    private val _tracksState = MutableStateFlow<Resource<List<Track>>>(Resource.Loading)
    val tracksState: StateFlow<Resource<List<Track>>> = _tracksState

    init {
        // Load sample tracks when ViewModel is initialized
        loadSampleTracks()
    }



    private fun loadSampleTracks() {
        // In a real app, you would fetch this from a repository
        val sampleTracks = listOf(
            Track(
                id = "1",
                title = "Blinding Lights",
                artist = "The Weeknd",
                albumArt = "",
                duration = 200000
            ),
            Track(
                id = "2",
                title = "Circles",
                artist = "Post Malone",
                albumArt = "",
                duration = 215000
            ),
            Track(
                id = "3",
                title = "Don't Start Now",
                artist = "Dua Lipa",
                albumArt = "",
                duration = 183000
            ),
            Track(
                id = "4",
                title = "Watermelon Sugar",
                artist = "Harry Styles",
                albumArt = "",
                duration = 174000
            ),
            Track(
                id = "5",
                title = "Levitating",
                artist = "Dua Lipa ft. DaBaby",
                albumArt = "",
                duration = 203000
            )
        )

        _tracksState.value = Resource.Success(sampleTracks)
    }

    fun clearData() {
        _tracksState.value = Resource.Loading
    }

    // In a real app, you would implement functions to fetch tracks from a remote source
    // For example:
    // fun fetchTracks() { ... }
}