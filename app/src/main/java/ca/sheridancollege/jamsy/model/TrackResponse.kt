package ca.sheridancollege.jamsy.model
import com.google.gson.annotations.SerializedName

data class TracksResponse(
    @SerializedName("tracks")
    val tracks: List<Track> = emptyList()
)