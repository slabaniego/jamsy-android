package ca.sheridancollege.jamsy.presentation.screens

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.util.Resource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("DiscoveryScreen-related JUnit 5 tests (no Android/Compose)")
class DiscoveryScreenTest {

    @Nested
    @DisplayName("Resource State Tests")
    inner class ResourceStateTests {

        @Test
        @DisplayName("Loading state is represented by Resource.Loading")
        fun resourceLoading_isCorrectType() {
            val state: Resource<List<Track>> = Resource.Loading
            assertTrue(state is Resource.Loading)
        }

        @Test
        @DisplayName("Resource.Success wraps Track list correctly")
        fun resourceSuccess_wrapsTracksCorrectly() {
            val tracks = listOf(
                Track(
                    id = "track1",
                    name = "Test Song",
                    artists = listOf("Artist One", "Artist Two"),
                    albumCover = "https://example.com/cover.jpg",
                    durationMs = 210000,
                    explicit = false,
                    popularity = 75
                ),
                Track(
                    id = "track2",
                    name = "Another Song",
                    artists = listOf("Solo Artist"),
                    albumCover = "https://example.com/cover2.jpg",
                    durationMs = 180000,
                    explicit = true,
                    popularity = 90
                )
            )

            val state: Resource<List<Track>> = Resource.Success(tracks)

            assertTrue(state is Resource.Success)
            val data = (state as Resource.Success).data
            assertEquals(2, data.size)
            assertEquals("track1", data[0].id)
            assertEquals("Test Song", data[0].name)
            assertEquals(2, data[0].artists.size)
        }

        @Test
        @DisplayName("Resource.Error contains error message")
        fun resourceError_containsMessage() {
            val errorMessage = "Failed to load discovery tracks"
            val state: Resource<List<Track>> = Resource.Error(errorMessage)

            assertTrue(state is Resource.Error)
            assertEquals(errorMessage, (state as Resource.Error).message)
        }
    }

    @Nested
    @DisplayName("Track Model Tests")
    inner class TrackModelTests {

        @Test
        @DisplayName("Track default values are set correctly")
        fun track_defaultValues() {
            val track = Track()

            assertNull(track.id)
            assertEquals("", track.name)
            assertEquals(emptyList<String>(), track.artists)
            assertFalse(track.explicit)
            assertEquals(0, track.popularity)
            assertEquals(0, track.durationMs)
            assertNull(track.albumCover)
            assertNull(track.previewUrl)
        }

        @Test
        @DisplayName("Track with all properties is created correctly")
        fun track_fullConstruction() {
            val track = Track(
                id = "spotify:track:abc123",
                externalUrl = "https://open.spotify.com/track/abc123",
                popularity = 85,
                name = "Awesome Track",
                isrc = "USRC12345678",
                explicit = true,
                previewUrl = "https://p.scdn.co/preview/abc123",
                albumCover = "https://i.scdn.co/image/abc123",
                artists = listOf("Main Artist", "Featured Artist"),
                genres = listOf("Pop", "Electronic"),
                artistName = "Main Artist",
                imageUrl = "https://i.scdn.co/image/abc123",
                durationMs = 245000
            )

            assertEquals("spotify:track:abc123", track.id)
            assertEquals("https://open.spotify.com/track/abc123", track.externalUrl)
            assertEquals(85, track.popularity)
            assertEquals("Awesome Track", track.name)
            assertEquals("USRC12345678", track.isrc)
            assertTrue(track.explicit)
            assertEquals("https://p.scdn.co/preview/abc123", track.previewUrl)
            assertEquals("https://i.scdn.co/image/abc123", track.albumCover)
            assertEquals(listOf("Main Artist", "Featured Artist"), track.artists)
            assertEquals(listOf("Pop", "Electronic"), track.genres)
            assertEquals("Main Artist", track.artistName)
            assertEquals("https://i.scdn.co/image/abc123", track.imageUrl)
            assertEquals(245000, track.durationMs)
        }

        @Test
        @DisplayName("Track explicit flag handles explicit content correctly")
        fun track_explicitFlag() {
            val explicitTrack = Track(name = "Explicit Song", explicit = true)
            val cleanTrack = Track(name = "Clean Song", explicit = false)

            assertTrue(explicitTrack.explicit)
            assertFalse(cleanTrack.explicit)
        }

        @Test
        @DisplayName("Track artists list can contain multiple artists")
        fun track_multipleArtists() {
            val track = Track(
                name = "Collaboration Song",
                artists = listOf("Artist A", "Artist B", "Artist C")
            )

            assertEquals(3, track.artists.size)
            assertEquals("Artist A", track.artists[0])
            assertEquals("Artist B", track.artists[1])
            assertEquals("Artist C", track.artists[2])
        }
    }

    @Nested
    @DisplayName("Swipe Threshold Logic Tests")
    inner class SwipeThresholdTests {

        private val swipeThresholdLike = 60
        private val swipeThresholdDislike = -60

        @Test
        @DisplayName("Swipe right beyond threshold triggers like action")
        fun swipeRight_triggersLike() {
            val offset = 65f
            val action = when {
                offset > swipeThresholdLike -> "like"
                offset < swipeThresholdDislike -> "dislike"
                else -> null
            }
            assertEquals("like", action)
        }

        @Test
        @DisplayName("Swipe left beyond threshold triggers dislike action")
        fun swipeLeft_triggersDislike() {
            val offset = -65f
            val action = when {
                offset > swipeThresholdLike -> "like"
                offset < swipeThresholdDislike -> "dislike"
                else -> null
            }
            assertEquals("dislike", action)
        }

        @Test
        @DisplayName("Swipe within threshold triggers no action")
        fun swipeWithinThreshold_noAction() {
            val offsets = listOf(0f, 30f, -30f, 59f, -59f)
            
            offsets.forEach { offset ->
                val action = when {
                    offset > swipeThresholdLike -> "like"
                    offset < swipeThresholdDislike -> "dislike"
                    else -> null
                }
                assertNull(action, "Offset $offset should not trigger any action")
            }
        }

        @Test
        @DisplayName("Swipe exactly at threshold does not trigger action")
        fun swipeAtExactThreshold_noAction() {
            val likeEdge = 60f
            val dislikeEdge = -60f

            val likeAction = when {
                likeEdge > swipeThresholdLike -> "like"
                likeEdge < swipeThresholdDislike -> "dislike"
                else -> null
            }
            
            val dislikeAction = when {
                dislikeEdge > swipeThresholdLike -> "like"
                dislikeEdge < swipeThresholdDislike -> "dislike"
                else -> null
            }

            assertNull(likeAction, "Exactly at like threshold should not trigger action")
            assertNull(dislikeAction, "Exactly at dislike threshold should not trigger action")
        }
    }
}



