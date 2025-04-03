package ca.sheridancollege.jamsy.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Specialized tag for genre displays
 */
@Composable
fun GenreTag(genre: String, modifier: Modifier = Modifier) {
    Tag(
        text = genre,
        backgroundColor = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}