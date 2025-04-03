package ca.sheridancollege.jamsy.ui.components

import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ExitToApp

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    selectedRoute: String = "",
    onHomeSelected: () -> Unit,
    onProfileSelected: () -> Unit = {},  // Already on profile
    onTrackListSelected: () -> Unit = {},
    onLogoutSelected: () -> Unit,

) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedRoute == "home",
            onClick = onHomeSelected
        )

        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Tracks") },
            label = { Text("Tracks") },
            selected = selectedRoute == "tracklist",
            onClick = onTrackListSelected
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = selectedRoute == "profile",
            onClick = onProfileSelected
        )

        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout") },
            label = { Text("Logout") },
            selected = false,
            onClick = onLogoutSelected
        )
    }
}