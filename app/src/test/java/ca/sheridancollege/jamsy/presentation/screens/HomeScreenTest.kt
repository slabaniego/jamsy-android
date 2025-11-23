package ca.sheridancollege.jamsy.presentation.screens

import ca.sheridancollege.jamsy.domain.models.User
import ca.sheridancollege.jamsy.util.Resource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("HomeScreen-related JUnit 5 tests (no Android/Compose)")
class HomeScreenTest {

    @Test
    @DisplayName("Loading state is represented by Resource.Loading")
    fun resourceLoading_isCorrectType() {
        val state: Resource<User> = Resource.Loading
        assertTrue(state is Resource.Loading)
    }

    @Test
    @DisplayName("Resource.Success wraps User data correctly")
    fun resourceSuccess_wrapsUserCorrectly() {
        val user = User(
            uid = "uid123",
            email = "test@example.com",
            profileImageBase64 = "base64data",
            displayName = "Test User",
            spotifyProfileImageUrl = "https://example.com/image.jpg",
            spotifySubscriptionType = "premium"
        )

        val state: Resource<User> = Resource.Success(user)

        assertTrue(state is Resource.Success)
        val data = (state as Resource.Success).data
        assertEquals("uid123", data.uid)
        assertEquals("test@example.com", data.email)
        assertEquals("Test User", data.displayName)
        assertEquals("https://example.com/image.jpg", data.spotifyProfileImageUrl)
        assertEquals("premium", data.spotifySubscriptionType)
    }
}


