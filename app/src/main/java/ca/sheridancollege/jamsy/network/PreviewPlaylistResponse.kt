package ca.sheridancollege.jamsy.network

import ca.sheridancollege.jamsy.model.Track

data class PreviewPlaylistResponse(
    val message: String? = null,
    val tracks: List<Track> = emptyList()
)
