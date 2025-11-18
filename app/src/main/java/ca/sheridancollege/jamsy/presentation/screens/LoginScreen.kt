package ca.sheridancollege.jamsy.presentation.screens

import android.widget.Toast

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ca.sheridancollege.jamsy.presentation.viewmodels.AuthViewModel
import ca.sheridancollege.jamsy.presentation.components.GlassCard
import ca.sheridancollege.jamsy.presentation.components.PremiumButton
import ca.sheridancollege.jamsy.presentation.theme.Gray
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyBlack
import ca.sheridancollege.jamsy.presentation.theme.SpotifyDarkGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.SpotifyMediumGray
import ca.sheridancollege.jamsy.presentation.theme.White
import ca.sheridancollege.jamsy.util.Resource


@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when(loginState) {
            is Resource.Success -> onLoginSuccess()
            is Resource.Error -> {
                errorMessage = (loginState as Resource.Error).message

                if (errorMessage.contains("Spotify")) {
                    Toast.makeText(
                        context,
                        "Spotify login failed: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (errorMessage.contains("HTML") || errorMessage.contains("login page")) {
                    Toast.makeText(
                        context,
                        "Server returned login page instead of data.",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (errorMessage.contains("Server error")) {
                    Toast.makeText(
                        context,
                        "Server error: Could not connect to authentication service.",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (errorMessage.contains("Failed to authenticate: Unknown error")) {
                    Toast.makeText(
                        context,
                        "Authentication failed: Please check your credentials and try again.",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (errorMessage.contains("Unknown")) {
                    Toast.makeText(
                        context,
                        "Authentication failed: Unable to connect to the server",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else -> {}
        }
    }

    LoginScreenContent(
        errorMessage = errorMessage,
        onNavigateToSignup = onNavigateToSignup,
        onConnectSpotifyClick = { viewModel.launchSpotifyAuth() },
        isLoading = loginState is Resource.Loading
    )
}

@Composable
private fun LoginScreenContent(
    errorMessage: String = "",
    onNavigateToSignup: () -> Unit = {},
    onConnectSpotifyClick: () -> Unit = {},
    isLoading: Boolean = false
) {
    // Animation states for entrance effects
    var titleAlpha by remember { mutableStateOf(0f) }
    var subtitleAlpha by remember { mutableStateOf(0f) }
    var cardAlpha by remember { mutableStateOf(0f) }
    var buttonAlpha by remember { mutableStateOf(0f) }
    var footerAlpha by remember { mutableStateOf(0f) }

    // Animated alpha values
    val animatedTitleAlpha by animateFloatAsState(
        targetValue = titleAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 100),
        label = "title_alpha"
    )
    val animatedSubtitleAlpha by animateFloatAsState(
        targetValue = subtitleAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 300),
        label = "subtitle_alpha"
    )
    val animatedCardAlpha by animateFloatAsState(
        targetValue = cardAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 500),
        label = "card_alpha"
    )
    val animatedButtonAlpha by animateFloatAsState(
        targetValue = buttonAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 700),
        label = "button_alpha"
    )
    val animatedFooterAlpha by animateFloatAsState(
        targetValue = footerAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 900),
        label = "footer_alpha"
    )

    LaunchedEffect(Unit) {
        titleAlpha = 1f
        subtitleAlpha = 1f
        cardAlpha = 1f
        buttonAlpha = 1f
        footerAlpha = 1f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SpotifyDarkGray,
                        SpotifyBlack,
                        SpotifyBlack
                    )
                )
            )
    ) {
        // Decorative gradient orbs for premium feel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SpotifyGreen.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title with gradient accent
            Text(
                text = "JAMSY",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = White,
                modifier = Modifier.alpha(animatedTitleAlpha)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle with gradient text effect
            Box(
                modifier = Modifier.alpha(animatedSubtitleAlpha)
            ) {
                Text(
                    text = "Discover Hidden Gems",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    ),
                    color = SpotifyGreen
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Premium glassmorphism feature card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animatedCardAlpha)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Uncover amazing artists",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "Our algorithm finds talented musicians with fewer than 50,000 monthly listeners",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Feature highlights in glassmorphism cards
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FeatureItem(
                            text = "🎵 Curated Discovery",
                            modifier = Modifier.fillMaxWidth()
                        )
                        FeatureItem(
                            text = "✨ Hidden Talent",
                            modifier = Modifier.fillMaxWidth()
                        )
                        FeatureItem(
                            text = "🚀 Premium Experience",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Error message with glassmorphism
            if (errorMessage.isNotEmpty()) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(animatedCardAlpha)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Premium gradient button
            PremiumButton(
                text = "CONNECT WITH SPOTIFY",
                onClick = onConnectSpotifyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .alpha(animatedButtonAlpha)
                    .padding(horizontal = 16.dp),
                enabled = !isLoading,
                fontSize = 16
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Sign up link with subtle animation
            TextButton(
                onClick = onNavigateToSignup,
                modifier = Modifier.alpha(animatedFooterAlpha)
            ) {
                Text(
                    text = "Don't have an account? Sign up",
                    color = LightGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer text with glassmorphism
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animatedFooterAlpha)
            ) {
                Text(
                    text = "Find your next favorite artist that nobody knows about yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * Reusable feature item component for glassmorphism cards.
 */
@Composable
private fun FeatureItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        SpotifyGreen.copy(alpha = 0.15f),
                        SpotifyGreen.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = LightGray,
            fontWeight = FontWeight.Medium
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        errorMessage = "",
        onNavigateToSignup = {},
        onConnectSpotifyClick = {},
        isLoading = false
    )
}
