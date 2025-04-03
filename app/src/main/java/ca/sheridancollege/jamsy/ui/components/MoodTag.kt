package ca.sheridancollege.jamsy.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Specialized tag for mood displays
 */
@Composable
fun MoodTag(mood: String, modifier: Modifier = Modifier) {
    Tag(
        text = mood,
        backgroundColor = MaterialTheme.colorScheme.secondary,
        modifier = modifier
    )
}