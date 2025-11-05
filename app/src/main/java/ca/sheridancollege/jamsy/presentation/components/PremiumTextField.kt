package ca.sheridancollege.jamsy.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen
import ca.sheridancollege.jamsy.presentation.theme.White

/**
 * Premium text field component with glassmorphism styling.
 */
@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SpotifyGreen,
            unfocusedBorderColor = LightGray.copy(alpha = 0.5f),
            focusedLabelColor = SpotifyGreen,
            unfocusedLabelColor = LightGray,
            focusedTextColor = White,
            unfocusedTextColor = White,
            cursorColor = SpotifyGreen,
            focusedContainerColor = White.copy(alpha = 0.05f),
            unfocusedContainerColor = White.copy(alpha = 0.03f)
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

