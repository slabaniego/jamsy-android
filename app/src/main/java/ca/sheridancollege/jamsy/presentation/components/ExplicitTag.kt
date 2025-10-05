package ca.sheridancollege.jamsy.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Specialized tag for explicit content warning
 */
@Composable
fun ExplicitTag(modifier: Modifier = Modifier) {
    Tag(
        text = "EXPLICIT",
        backgroundColor = Color(0xFFE53935),
        modifier = modifier
    )
}