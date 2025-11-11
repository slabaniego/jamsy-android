package ca.sheridancollege.jamsy.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White

/**
 * Premium reusable header component with glassmorphic design.
 * Can be used across multiple screens with customizable title and subtitle.
 * Displays navigation controls and customizable trailing content.
 *
 * @param title Header title text
 * @param subtitle Header subtitle text
 * @param onBack Callback when back button is clicked
 * @param animationDelay Delay for entrance animation in milliseconds
 * @param showBackButton Whether to show the back button
 * @param trailingContent Optional composable for custom content on the right side (avatar, buttons, etc)
 */
@Composable
fun PremiumHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit = {},
    animationDelay: Int = 100,
    showBackButton: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null
) {
    // Animation state
    var titleAlpha by remember { mutableFloatStateOf(0f) }
    
    val animatedAlpha by animateFloatAsState(
        targetValue = titleAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = animationDelay),
        label = "header_alpha"
    )
    
    LaunchedEffect(Unit) {
        titleAlpha = 1f
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(animatedAlpha)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button - only show if enabled
            if (showBackButton) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    SpotifyGreen.copy(alpha = 0.2f),
                                    SpotifyGreen.copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SpotifyGreen
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))
            }

            // Title and subtitle column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    maxLines = 1
                )
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = LightGray,
                    maxLines = 2
                )
            }

            // Trailing content (avatar, button, or custom content)
            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(12.dp))
                trailingContent()
            }
        }
    }
}

/**
 * Overload for backward compatibility - with built-in action button
 * 
 * This version is convenient for simple cases where you just need an action button
 */
@Composable
fun PremiumHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit = {},
    onActionClick: () -> Unit,
    actionButtonText: String = "View",
    actionButtonEnabled: Boolean = true,
    animationDelay: Int = 100,
    showBackButton: Boolean = true,
    spotifyImageUrl: String? = null,
    localImageBase64: String? = null,
    isLoadingImage: Boolean = false
) {
    PremiumHeader(
        title = title,
        subtitle = subtitle,
        onBack = onBack,
        animationDelay = animationDelay,
        showBackButton = showBackButton,
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar - show if image data provided
                if (!spotifyImageUrl.isNullOrEmpty() || !localImageBase64.isNullOrEmpty()) {
                    HeaderAvatar(
                        spotifyImageUrl = spotifyImageUrl,
                        localImageBase64 = localImageBase64,
                        isLoading = isLoadingImage
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Action button - only show if text is not empty
                if (actionButtonText.isNotEmpty()) {
                    PremiumButton(
                        text = actionButtonText,
                        onClick = onActionClick,
                        enabled = actionButtonEnabled,
                        fontSize = 12
                    )
                }
            }
        }
    )
}

