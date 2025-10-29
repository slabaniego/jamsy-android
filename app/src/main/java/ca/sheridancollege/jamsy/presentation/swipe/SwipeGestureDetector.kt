package ca.sheridancollege.jamsy.presentation.swipe

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Functional interface for detecting and handling swipe gestures.
 * Follows the Single Responsibility Principle by isolating gesture detection.
 */
fun interface SwipeGestureListener {
    suspend fun onDrag(dragAmount: Dp, totalOffset: Dp)
}

/**
 * Configuration for swipe gesture detection
 * Encapsulates swipe behavior parameters for reusability
 */
data class SwipeGestureConfig(
    val onDragStart: () -> Unit = {},
    val onDragUpdate: (dragAmount: Float) -> Unit = {},
    val onDragEnd: (finalOffset: Float) -> Unit = {},
    val verticalDragable: Boolean = false
)

/**
 * Detects swipe gestures on a composable
 * Responsible for translating raw pointer input into swipe events
 * 
 * Usage:
 * ```
 * Modifier.pointerInput(Unit) {
 *     detectSwipeGesture(config)
 * }
 * ```
 */
suspend fun PointerInputScope.detectSwipeGesture(config: SwipeGestureConfig) {
    detectDragGestures(
        onDragStart = { config.onDragStart() },
        onDragEnd = { 
            // Get the final offset from the gesture - requires state management at caller
        },
        onDrag = { change, dragAmount ->
            config.onDragUpdate(dragAmount.x)
        }
    )
}

/**
 * Extended version with finish callback for swipe completion
 * Provides full lifecycle hooks for swipe gestures
 */
suspend fun PointerInputScope.detectSwipeGestureWithEnd(
    onDragStart: () -> Unit = {},
    onDragUpdate: (Float) -> Unit = {},
    onDragEnd: () -> Unit = {}
) {
    detectDragGestures(
        onDragStart = { onDragStart() },
        onDragEnd = { onDragEnd() },
        onDrag = { _, dragAmount ->
            onDragUpdate(dragAmount.x)
        }
    )
}
