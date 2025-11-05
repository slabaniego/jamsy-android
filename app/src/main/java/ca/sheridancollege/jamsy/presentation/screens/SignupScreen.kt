package ca.sheridancollege.jamsy.presentation.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.launch

import ca.sheridancollege.jamsy.presentation.viewmodels.AuthViewModel
import ca.sheridancollege.jamsy.presentation.components.GlassCard
import ca.sheridancollege.jamsy.presentation.components.PremiumGradientButton
import ca.sheridancollege.jamsy.presentation.components.PremiumTextField
import ca.sheridancollege.jamsy.presentation.theme.Gray
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyBlack
import ca.sheridancollege.jamsy.presentation.theme.SpotifyDarkGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White
import ca.sheridancollege.jamsy.util.Resource

@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    onSignupSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val signupState by viewModel.signupState.collectAsState()

    LaunchedEffect(signupState) {
        when(signupState) {
            is Resource.Success -> onSignupSuccess()
            is Resource.Error -> errorMessage = (signupState as Resource.Error).message
            else -> {}
        }
    }

    SignupScreenContent(
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        confirmPassword = confirmPassword,
        onConfirmPasswordChange = { confirmPassword = it },
        errorMessage = errorMessage,
        onSignupClick = {
            errorMessage = ""
            if (password != confirmPassword) {
                errorMessage = "Passwords don't match"
            } else {
                coroutineScope.launch {
                    viewModel.signup(email, password)
                }
            }
        },
        onNavigateToLogin = onNavigateToLogin,
        isLoading = signupState is Resource.Loading
    )
}

@Composable
private fun SignupScreenContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    errorMessage: String,
    onSignupClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    isLoading: Boolean = false
) {
    // Animation states for entrance effects
    var titleAlpha by remember { mutableStateOf(0f) }
    var subtitleAlpha by remember { mutableStateOf(0f) }
    var formAlpha by remember { mutableStateOf(0f) }
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
    val animatedFormAlpha by animateFloatAsState(
        targetValue = formAlpha,
        animationSpec = tween(durationMillis = 800, delayMillis = 500),
        label = "form_alpha"
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
        formAlpha = 1f
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
            // Title with premium styling
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = White,
                modifier = Modifier.alpha(animatedTitleAlpha)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle with gradient accent
            Box(
                modifier = Modifier.alpha(animatedSubtitleAlpha)
            ) {
                Text(
                    text = "Join JAMSY",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    ),
                    color = SpotifyGreen
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Premium glassmorphism form card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animatedFormAlpha)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PremiumTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = "Email",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    PremiumTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = "Password",
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    PremiumTextField(
                        value = confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        label = "Confirm Password",
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message with glassmorphism
            if (errorMessage.isNotEmpty()) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(animatedFormAlpha)
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
            PremiumGradientButton(
                text = "SIGN UP",
                onClick = onSignupClick,
                isLoading = isLoading,
                modifier = Modifier
                    .alpha(animatedButtonAlpha)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Login link with subtle animation
            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.alpha(animatedFooterAlpha)
            ) {
                Text(
                    text = "Already have an account? Log in",
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start discovering hidden gems today",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
