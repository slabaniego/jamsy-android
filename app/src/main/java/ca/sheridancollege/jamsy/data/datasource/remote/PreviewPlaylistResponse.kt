package ca.sheridancollege.jamsy.data.datasource.remote

import ca.sheridancollege.jamsy.domain.models.Track

data class PreviewPlaylistResponse(
    val message: String? = null,
    val tracks: List<Track> = emptyList()
)
