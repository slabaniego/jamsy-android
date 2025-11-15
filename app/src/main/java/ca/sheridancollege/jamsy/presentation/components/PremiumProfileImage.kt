package ca.sheridancollege.jamsy.presentation.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White

/**
 * Premium profile image component with glassmorphism border.
 */
@Composable
fun PremiumProfileImage(
    spotifyImageUrl: String,
    localImageBase64: String,
    isLoading: Boolean,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(
                width = 3.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        SpotifyGreen,
                        SpotifyGreen.copy(alpha = 0.7f),
                        SpotifyGreen
                    )
                ),
                shape = CircleShape
            )
            .clickable { onImageClick() },
        contentAlignment = Alignment.Center
    ) {
        // Priority 1: Display Spotify profile image
        if (spotifyImageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(model = spotifyImageUrl),
                contentDescription = "Spotify Profile Image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        // Priority 2: Display local Base64 profile image
        else if (localImageBase64.isNotEmpty()) {
            val bitmap = remember(localImageBase64) {
                decodeBase64Image(localImageBase64)
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Local Profile Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                PlaceholderText()
            }
        }
        // Priority 3: Show placeholder
        else {
            PlaceholderText()
        }

        // Show loading indicator during upload
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = SpotifyGreen,
                    modifier = Modifier.size(size * 0.45f)
                )
            }
        }
    }
}

/**
 * Helper function to decode Base64 image outside of composable context.
 */
private fun decodeBase64Image(base64String: String): ImageBitmap? {
    return try {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

@Composable
private fun PlaceholderText() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tap to add photo",
            style = MaterialTheme.typography.bodySmall,
            color = White.copy(alpha = 0.7f)
        )
    }
}
