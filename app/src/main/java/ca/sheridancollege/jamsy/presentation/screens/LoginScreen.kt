package ca.sheridancollege.jamsy.presentation.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import ca.sheridancollege.jamsy.config.AppConfig
import ca.sheridancollege.jamsy.presentation.viewmodels.AuthViewModel
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1F1F1F),
                        Color(0xFF121212)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "JAMSY",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Discover Hidden Gems",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1DB954)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Uncover amazing artists that fly under the radar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Our algorithm finds talented musicians with fewer than 50,000 monthly listeners",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Button(
                onClick = {
                    errorMessage = ""
                    viewModel.launchSpotifyAuth()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1DB954)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                if (loginState is Resource.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("CONNECT WITH SPOTIFY", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToSignup) {
                Text("Don't have an account? Sign up", color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Find your next favorite artist that nobody knows about yet",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

private fun launchSpotifyLogin(context: android.content.Context) {
    val clientId = "024491d2f5bb43fb93ff6c69eacf6ab8"

    val redirectUri = AppConfig.SPOTIFY_AUTH_REDIRECT_URI

    val scope = "user-top-read user-library-read user-read-recently-played"

    val spotifyAuthUrl = "https://accounts.spotify.com/authorize" +
            "?client_id=$clientId" +
            "&response_type=code" +
            "&redirect_uri=${Uri.encode(redirectUri)}" +
            "&scope=${Uri.encode(scope)}" +
            "&show_dialog=true"

    try {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, Uri.parse(spotifyAuthUrl))
    } catch (e: Exception) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(spotifyAuthUrl))
            context.startActivity(intent)
        } catch (e2: Exception) {
            Toast.makeText(
                context,
                "Unable to open browser for authentication",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}