package ca.sheridancollege.jamsy.domain.models

/**
 * Represents the state of a card during a swipe gesture.
 * Follows SOLID principles by separating state representation from UI logic.
 *
 * @param offsetX Horizontal offset from drag gesture
 * @param offsetY Vertical offset from drag gesture
 * @param rotation Rotation angle in degrees
 * @param scale Scale factor for card
 * @param alpha Transparency value (0f-1f)
 */
data class SwipeCardState(
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val rotation: Float = 0f,
    val scale: Float = 1f,
    val alpha: Float = 1f
) {
    /**
     * Determines the swipe direction based on offset
     * @return SwipeDirection or null if no significant swipe
     */
    fun getSwipeDirection(threshold: Float = 100f): SwipeDirection? = when {
        offsetX > threshold -> SwipeDirection.RIGHT
        offsetX < -threshold -> SwipeDirection.LEFT
        offsetY > threshold -> SwipeDirection.DOWN
        offsetY < -threshold -> SwipeDirection.UP
        else -> null
    }

    /**
     * Calculates if swipe should be accepted based on threshold
     */
    fun isSwipeThresholdCrossed(threshold: Float = 200f): Boolean =
        kotlin.math.abs(offsetX) > threshold
}

/**
 * Represents possible swipe directions
 */
enum class SwipeDirection {
    LEFT, RIGHT, UP, DOWN
}

/**
 * Represents the result of a swipe action
 * Encapsulates what happened after a swipe completes
 */
sealed class SwipeResult {
    data class Swiped(val direction: SwipeDirection, val card: Track) : SwipeResult()
    object Cancelled : SwipeResult()
    object None : SwipeResult()
}
