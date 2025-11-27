/*
 * Theme.kt
 * Application-wide Material 3 color scheme and Jamsy theme setup.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SpotifyGreen,
    secondary = SpotifyMediumGray,
    tertiary = SpotifyDarkGray,
    background = SpotifyBlack,
    surface = SpotifyDarkGray,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = SpotifyGreen,
    secondary = SpotifyMediumGray,
    tertiary = SpotifyDarkGray,
    background = SpotifyBlack,
    surface = SpotifyDarkGray,
    onBackground = White,
    onSurface = White
)

@Composable
fun JamsyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}