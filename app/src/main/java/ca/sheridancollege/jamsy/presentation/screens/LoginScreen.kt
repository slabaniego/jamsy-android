package ca.sheridancollege.jamsy.presentation.screens

import android.widget.Toast

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

import ca.sheridancollege.jamsy.presentation.viewmodels.AuthViewModel
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SpotifyDarkGray,
                        SpotifyBlack
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
                color = White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Discover Hidden Gems",
                style = MaterialTheme.typography.titleMedium,
                color = SpotifyGreen
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SpotifyMediumGray
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
                        color = White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Our algorithm finds talented musicians with fewer than 50,000 monthly listeners",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightGray,
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
                onClick = onConnectSpotifyClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SpotifyGreen
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("CONNECT WITH SPOTIFY", color = White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToSignup) {
                Text("Don't have an account? Sign up", color = LightGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Find your next favorite artist that nobody knows about yet",
                style = MaterialTheme.typography.bodySmall,
                color = Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
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
