package ca.sheridancollege.jamsy.presentation.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import ca.sheridancollege.jamsy.presentation.Screen
import ca.sheridancollege.jamsy.presentation.components.BottomBar
import ca.sheridancollege.jamsy.presentation.viewmodels.ProfileViewModel
import ca.sheridancollege.jamsy.util.PermissionHandler
import ca.sheridancollege.jamsy.util.Resource


private const val TAG = "ProfileScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val profileState by viewModel.profileState.collectAsState()
    val uploadState by viewModel.uploadState.collectAsState()
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Load profile when the screen is first shown
    LaunchedEffect(Unit) {
        viewModel.getUserProfile()
    }

    // Handle upload result
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is Resource.Success -> {
                snackbarMessage = "Profile image updated successfully!"
                showSnackbar = true
                viewModel.getUserProfile()  // Refresh profile data
                viewModel.resetUploadState()
            }
            is Resource.Error -> {
                Log.e(TAG, "Upload error: ${(uploadState as Resource.Error).message}")
                snackbarMessage = (uploadState as Resource.Error).message
                showSnackbar = true
            }
            else -> {}
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.d(TAG, "Selected image URI: $uri")
            viewModel.uploadProfileImage(context, uri)
        } else {
            Log.d(TAG, "No image selected")
        }
    }

    // Permission request launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Storage permission granted")
            imagePickerLauncher.launch("image/*")
        } else {
            Log.e(TAG, "Storage permission denied")
            snackbarMessage = "Storage permission denied. Cannot access gallery."
            showSnackbar = true
        }
    }

    // Function to handle image selection with permission check
    val selectImage = {
        if (PermissionHandler.hasStoragePermission(context)) {
            Log.d(TAG, "Already has storage permission, launching picker")
            imagePickerLauncher.launch("image/*")
        } else {
            Log.d(TAG, "Requesting storage permission")
            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") }
            )
        },
        bottomBar = {
            BottomBar(
                selectedRoute = Screen.Profile.route,
                onHomeSelected = onNavigateToHome,
                onProfileSelected = {},  // Already on profile
                onLogoutSelected = onLogout
            )
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(snackbarMessage)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (profileState) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }
                is Resource.Success -> {
                    val user = (profileState as Resource.Success).data
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile image
                        Box(
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .clickable { selectImage() },
                            contentAlignment = Alignment.Center
                        ) {
                            // Display profile image using Base64 string
                            if (user.profileImageBase64.isNotEmpty()) {
                                val imageBytes = Base64.decode(user.profileImageBase64, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Profile Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text("Invalid image data")
                                }
                            } else {
                                Text("Tap to add photo")
                            }

                            // Show loading indicator during upload
                            if (uploadState is Resource.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Tap to change photo button
                        Button(onClick = { selectImage() }) {
                            Text("Change Profile Photo")
                        }
                    }
                }
                is Resource.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error loading profile: ${(profileState as Resource.Error).message}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = { viewModel.getUserProfile() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}