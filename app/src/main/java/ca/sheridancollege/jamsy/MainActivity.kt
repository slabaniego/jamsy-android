package ca.sheridancollege.jamsy

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import ca.sheridancollege.jamsy.navigation.NavGraph
import ca.sheridancollege.jamsy.util.PermissionHandler
import ca.sheridancollege.jamsy.ui.theme.JamsyTheme

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Debug permissions at startup
        debugPermissions()

        setContent {
            JamsyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }

    private fun debugPermissions() {
        Log.d(TAG, "Has storage permission: ${PermissionHandler.hasStoragePermission(this)}")

        // List all granted permissions
        val permissions = packageManager.getPackageInfo(
            packageName,
            PackageManager.GET_PERMISSIONS
        ).requestedPermissions

        Log.d(TAG, "Requested permissions:")
        permissions?.forEach { permission ->
            val granted = ActivityCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "$permission: ${if (granted) "GRANTED" else "DENIED"}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "Permission result received - Code: $requestCode")
        for (i in permissions.indices) {
            Log.d(TAG, "Permission: ${permissions[i]}, Result: ${
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) "GRANTED" else "DENIED"
            }")
        }
    }
}




