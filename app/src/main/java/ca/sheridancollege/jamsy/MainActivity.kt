package ca.sheridancollege.jamsy
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ca.sheridancollege.jamsy.navigation.NavGraph
import ca.sheridancollege.jamsy.ui.theme.JamsyTheme
import ca.sheridancollege.jamsy.util.Resource
import ca.sheridancollege.jamsy.viewmodel.AuthViewModel
import ca.sheridancollege.jamsy.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Use the ViewModelFactory
        val factory = ViewModelFactory()
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]


        handleIntent(intent)

        setContent {
            JamsyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel(factory = factory)

                    val authState by authViewModel.authState.collectAsState()

                    LaunchedEffect(authState) {
                        when (authState) {
                            is Resource.Success -> {
                                val user = (authState as Resource.Success).data
                                Log.d(TAG, "Auth state changed: user=${user?.email ?: "null"}")
                            }
                            is Resource.Loading -> {
                                Log.d(TAG, "Auth state is loading...")
                            }
                            is Resource.Error -> {
                                val error = (authState as Resource.Error).message
                                Log.e(TAG, "Auth state error: $error")
                            }
                        }
                    }

                    NavGraph(navController = navController)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.toString().startsWith("jamsy://callback")) {
            Log.d(TAG, "Received intent with data: $data")
            val code = data.getQueryParameter("code")
            val error = data.getQueryParameter("error")

            if (code != null) {
                Log.d(TAG, "Received Spotify auth code")
                authViewModel.handleSpotifyRedirect(code)
            } else if (error != null) {
                Log.e(TAG, "Spotify auth error: $error")
            }
        }
    }
}