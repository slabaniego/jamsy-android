package ca.sheridancollege.jamsy.ui.screens
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ca.sheridancollege.jamsy.config.AppConfig
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.viewmodel.AuthViewModel


@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                coroutineScope.launch {
                    viewModel.login(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            if (loginState is Resource.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(modifier = Modifier.weight(1f))
            Text(
                text = "OR",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Divider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                launchSpotifyLogin(context)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1DB954)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Continue with Spotify")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToSignup) {
            Text("Don't have an account? Sign up")
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