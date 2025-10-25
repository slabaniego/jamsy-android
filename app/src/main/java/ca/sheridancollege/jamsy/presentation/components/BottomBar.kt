package ca.sheridancollege.jamsy.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import ca.sheridancollege.jamsy.presentation.theme.LightGray
import ca.sheridancollege.jamsy.presentation.theme.SpotifyBlack
import ca.sheridancollege.jamsy.presentation.theme.SpotifyGreen

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    selectedRoute: String = "",
    onHomeSelected: () -> Unit,
    onProfileSelected: () -> Unit = {},
    onTrackListSelected: () -> Unit = {},
    onSearchSelected: () -> Unit = {},
    onLogoutSelected: () -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = SpotifyBlack
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedRoute == "home",
            onClick = onHomeSelected,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SpotifyGreen,
                selectedTextColor = SpotifyGreen,
                indicatorColor = SpotifyBlack,
                unselectedIconColor = LightGray,
                unselectedTextColor = LightGray
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = selectedRoute == "search",
            onClick = onSearchSelected,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SpotifyGreen,
                selectedTextColor = SpotifyGreen,
                indicatorColor = SpotifyBlack,
                unselectedIconColor = LightGray,
                unselectedTextColor = LightGray
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Tracks") },
            label = { Text("Tracks") },
            selected = selectedRoute == "tracklist",
            onClick = onTrackListSelected,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SpotifyGreen,
                selectedTextColor = SpotifyGreen,
                indicatorColor = SpotifyBlack,
                unselectedIconColor = LightGray,
                unselectedTextColor = LightGray
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = selectedRoute == "profile",
            onClick = onProfileSelected,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SpotifyGreen,
                selectedTextColor = SpotifyGreen,
                indicatorColor = SpotifyBlack,
                unselectedIconColor = LightGray,
                unselectedTextColor = LightGray
            )
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout") },
            label = { Text("Logout") },
            selected = false,
            onClick = onLogoutSelected,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SpotifyGreen,
                selectedTextColor = SpotifyGreen,
                indicatorColor = SpotifyBlack,
                unselectedIconColor = LightGray,
                unselectedTextColor = LightGray
            )
        )
    }
}