package ca.sheridancollege.jamsy.presentation.screens

import android.net.Uri
import android.util.Log

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import ca.sheridancollege.jamsy.presentation.Screen
import ca.sheridancollege.jamsy.presentation.components.BottomBar
import ca.sheridancollege.jamsy.presentation.components.GlassCard
import ca.sheridancollege.jamsy.presentation.components.PremiumGradientButton
import ca.sheridancollege.jamsy.presentation.components.PremiumProfileImage
import ca.sheridancollege.jamsy.presentation.components.ProfileInfoCard
import ca.sheridancollege.jamsy.presentation.components.PremiumHeader
import ca.sheridancollege.jamsy.presentation.components.PremiumButton
import ca.sheridancollege.jamsy.domain.models.User
import ca.sheridancollege.jamsy.presentation.theme.SpotifyBlack
import ca.sheridancollege.jamsy.presentation.theme.SpotifyDarkGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.viewmodels.ProfileViewModel
import ca.sheridancollege.jamsy.util.PermissionHandler
import ca.sheridancollege.jamsy.util.Resource


private const val TAG = "ProfileScreen"

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
        topBar = {},
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
        ProfileScreenContent(
            profileState = profileState,
            uploadState = uploadState,
            onSelectImage = selectImage,
            onRetry = { viewModel.getUserProfile() },
            onNavigateToHome = onNavigateToHome,
            paddingValues = paddingValues
        )
    }
}

@Composable
private fun ProfileScreenContent(
    profileState: Resource<User>,
    uploadState: Resource<String>?,
    onSelectImage: () -> Unit,
    onRetry: () -> Unit,
    onNavigateToHome: () -> Unit,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    // Animation states for entrance effects
    var imageAlpha by remember { mutableStateOf(0f) }
    var infoAlpha by remember { mutableStateOf(0f) }
    var buttonAlpha by remember { mutableStateOf(0f) }

    // Animated alpha values
    val animatedImageAlpha by animateFloatAsState(
        targetValue = imageAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 100),
        label = "image_alpha"
    )
    val animatedInfoAlpha by animateFloatAsState(
        targetValue = infoAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 300),
        label = "info_alpha"
    )
    val animatedButtonAlpha by animateFloatAsState(
        targetValue = buttonAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 500),
        label = "button_alpha"
    )

    LaunchedEffect(profileState) {
        if (profileState is Resource.Success) {
            imageAlpha = 1f
            infoAlpha = 1f
            buttonAlpha = 1f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
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
            modifier = Modifier.fillMaxSize()
        ) {
            // Premium header with back button
            PremiumHeader(
                title = "Profile",
                subtitle = "Manage your account",
                onBack = onNavigateToHome,
                animationDelay = 100,
                showBackButton = true,
                trailingContent = null
            )

            Box(
                modifier = Modifier.weight(1f)
            ) {
                when (profileState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SpotifyGreen)
                    }
                }
                is Resource.Success -> {
                    val user = profileState.data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                    // Premium profile image with glassmorphism border
                    PremiumProfileImage(
                        spotifyImageUrl = user.spotifyProfileImageUrl,
                        localImageBase64 = user.profileImageBase64,
                        isLoading = uploadState is Resource.Loading,
                        onImageClick = onSelectImage,
                        modifier = Modifier.alpha(animatedImageAlpha)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Profile info card with glassmorphism
                    ProfileInfoCard(
                        displayName = user.displayName,
                        subscriptionType = user.spotifySubscriptionType,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(animatedInfoAlpha)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Premium button
                    PremiumButton(
                        text = if (uploadState is Resource.Loading) "Uploading..." else "Change Profile Photo",
                        onClick = onSelectImage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(animatedButtonAlpha),
                        enabled = uploadState !is Resource.Loading,
                        fontSize = 14
                    )
                    }
                }
                is Resource.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Error loading profile",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = profileState.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        PremiumButton(
                            text = "Try Again",
                            onClick = onRetry,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = true,
                            fontSize = 14
                        )
                    }
                }
                }
            }
        }
    }
}