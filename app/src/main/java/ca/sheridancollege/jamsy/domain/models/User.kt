package ca.sheridancollege.jamsy.domain.models

data class User(
    val uid: String = "",
    val email: String = "",
    val profileImageBase64: String = "",
    val displayName: String = "",
    val spotifyProfileImageUrl: String = "",
    val spotifySubscriptionType: String = "" // "premium" or "free"
)