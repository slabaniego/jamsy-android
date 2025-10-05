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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController

import dagger.hilt.android.AndroidEntryPoint

import ca.sheridancollege.jamsy.presentation.navigation.NavGraph
import ca.sheridancollege.jamsy.presentation.theme.JamsyTheme
import ca.sheridancollege.jamsy.presentation.viewmodels.AuthViewModel
import ca.sheridancollege.jamsy.util.Resource

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JamsyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()

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

                    // Handle OAuth callback when authViewModel is available
                    LaunchedEffect(Unit) {
                        handleIntent(intent, authViewModel)
                    }

                    NavGraph(navController = navController)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { newIntent ->
            // Get the current authViewModel from the composition
            // This is a workaround since we can't access the ViewModel directly
            Log.d(TAG, "Received new intent with data: ${newIntent.data}")
        }
    }

    private fun handleIntent(intent: Intent, authViewModel: AuthViewModel) {
        val data: Uri? = intent.data
        if (data != null && data.toString().startsWith("jamsy://callback")) {
            Log.d(TAG, "Received intent with data: $data")
            authViewModel.handleOAuthRedirect(data)
        }
    }
}