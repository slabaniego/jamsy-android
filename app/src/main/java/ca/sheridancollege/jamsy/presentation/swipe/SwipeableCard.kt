package ca.sheridancollege.jamsy.presentation.swipe

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

/**
 * Configuration for SwipeableCard behavior
 * Encapsulates all swipe parameters for reusability and testability
 */
data class SwipeCardConfig(
    val rotationDegrees: Float = 15f,
    val alphaThreshold: Float = 800f,
    val swipeThreshold: Float = 150f, // Increased threshold for better mouse support
    val animationDurationMs: Int = 200, // Slightly longer animation for smoother feel
    val enableVerticalSwipe: Boolean = false,
    val enableMouseSupport: Boolean = true // Explicit mouse support flag
)

/**
 * Generic SwipeableCard composable
 * Follows Open/Closed Principle - open for extension, closed for modification
 * Works with any card content through composition
 * Improved mouse support for virtual device testing
 *
 * @param modifier Modifier for styling
 * @param dragOffset Current horizontal drag offset
 * @param onDragUpdate Called when drag updates
 * @param onDragEnd Called when drag ends with final offset
 * @param onSwipe Called when swipe gesture is completed (left/right)
 * @param config Configuration for swipe behavior
 * @param content Composable lambda for card content
 */
@Composable
fun SwipeableCard(
    modifier: Modifier = Modifier,
    dragOffset: Float,
    onDragUpdate: (Float) -> Unit,
    onDragEnd: (Float) -> Unit,
    onSwipe: (SwipeDirection) -> Unit,
    config: SwipeCardConfig = SwipeCardConfig(),
    content: @Composable () -> Unit
) {
    // Internal drag state for better mouse support
    var internalDragOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    
    // Use internal offset if dragging, otherwise use external offset
    val currentOffset = if (isDragging) internalDragOffset else dragOffset
    
    // Animate rotation based on drag offset
    val rotation by animateFloatAsState(
        targetValue = (currentOffset / 12f).coerceIn(-config.rotationDegrees, config.rotationDegrees),
        animationSpec = tween(durationMillis = config.animationDurationMs),
        label = "SwipeCardRotation"
    )

    // Calculate alpha based on offset
    val alpha = (1f - (abs(currentOffset) / config.alphaThreshold)).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = currentOffset
                rotationZ = rotation
                this.alpha = alpha
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { 
                        isDragging = true
                        internalDragOffset = dragOffset
                    },
                    onDragEnd = { 
                        isDragging = false
                        onDragEnd(internalDragOffset)
                        
                        // Determine swipe direction based on final offset
                        if (abs(internalDragOffset) > config.swipeThreshold) {
                            val direction = if (internalDragOffset > 0) SwipeDirection.RIGHT else SwipeDirection.LEFT
                            onSwipe(direction)
                        } else {
                            // Reset to center if swipe threshold not met
                            onDragUpdate(0f)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Calculate new offset properly - don't accumulate incorrectly
                        val newOffset = internalDragOffset + dragAmount.x
                        internalDragOffset = newOffset
                        onDragUpdate(newOffset)
                    }
                )
            }
    ) {
        content()
    }
}

/**
 * SwipeDirection enum for swipe classification
 */
enum class SwipeDirection {
    LEFT, RIGHT
}

/**
 * Extension function for visual feedback during swipe
 * Returns a pair of (hasLeftSwipeIndicator, hasRightSwipeIndicator)
 */
fun Float.getSwipeIndicators(threshold: Float = 50f): Pair<Boolean, Boolean> {
    return Pair(
        this < -threshold,  // Left swipe
        this > threshold    // Right swipe
    )
}
