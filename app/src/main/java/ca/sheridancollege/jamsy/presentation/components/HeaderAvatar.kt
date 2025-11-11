    package ca.sheridancollege.jamsy.presentation.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen

/**
 * Standalone header avatar component.
 * Displays user profile image with premium styling.
 *
 * @param spotifyImageUrl Spotify profile image URL
 * @param localImageBase64 Local image in base64 format
 * @param isLoading Whether the image is loading
 */
@Composable
fun HeaderAvatar(
    spotifyImageUrl: String?,
    localImageBase64: String?,
    isLoading: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .border(2.dp, SpotifyGreen, CircleShape)
            .background(Color.Gray.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = SpotifyGreen,
                modifier = Modifier.size(40.dp),
                strokeWidth = 2.dp
            )
        } else {
            // Priority 1: Display Spotify profile image
            if (!spotifyImageUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = spotifyImageUrl),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            // Priority 2: Display local Base64 profile image
            else if (!localImageBase64.isNullOrEmpty()) {
                val bitmap = remember(localImageBase64) {
                    decodeBase64Image(localImageBase64)
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

/**
 * Helper function to decode Base64 image.
 */
private fun decodeBase64Image(base64String: String): androidx.compose.ui.graphics.ImageBitmap? {
    return try {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

