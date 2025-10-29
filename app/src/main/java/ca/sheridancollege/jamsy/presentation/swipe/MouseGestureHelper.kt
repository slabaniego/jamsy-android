package ca.sheridancollege.jamsy.presentation.swipe

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerType
import kotlin.math.abs

/**
 * Helper utilities for better mouse gesture support in virtual devices
 * Addresses common issues with mouse input on Android emulators
 */

/**
 * Configuration for mouse-friendly swipe behavior
 */
data class MouseSwipeConfig(
    val enableTapFallback: Boolean = true,
    val doubleTapThreshold: Long = 300L,
    val swipeThreshold: Float = 150f,
    val enableHoverEffects: Boolean = true
)

/**
 * Extension function to check if current input is likely from mouse
 * This can help adjust behavior for mouse vs touch input
 */
fun PointerInputChange.isMouseInput(): Boolean {
    return this.type == PointerType.Mouse
}

/**
 * Enhanced swipe detection with mouse-specific optimizations
 * This function provides better mouse support for swipe gestures
 */
suspend fun PointerInputScope.detectMouseOptimizedSwipe(
    config: MouseSwipeConfig = MouseSwipeConfig(),
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    onDragUpdate: (Float) -> Unit = {}
) {
    var startTime = 0L
    var startPosition = Offset.Zero
    var totalDragDistance = 0f
    
    detectDragGestures(
        onDragStart = { offset ->
            startTime = System.currentTimeMillis()
            startPosition = offset
            totalDragDistance = 0f
        },
        onDragEnd = { 
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            
            // Determine swipe direction based on total drag distance
            if (abs(totalDragDistance) > config.swipeThreshold) {
                if (totalDragDistance > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }
            }
        },
        onDrag = { change, dragAmount ->
            totalDragDistance += dragAmount.x
            onDragUpdate(dragAmount.x)
        }
    )
}

/**
 * Modifier that adds enhanced mouse support for swipe gestures
 * Includes tap-to-swipe fallback for mouse users
 */
fun Modifier.enhancedSwipeSupport(
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    swipeThreshold: Float = 150f
): Modifier = this.pointerInput(Unit) {
    detectTapGestures(
        onDoubleTap = { 
            // Double tap as fallback for mouse users
            // Could be configured to trigger like/dislike
            // For now, we'll use right swipe as default for double tap
            onSwipeRight()
        }
    )
}
