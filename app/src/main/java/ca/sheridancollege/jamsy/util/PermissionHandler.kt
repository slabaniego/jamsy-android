/*
 * PermissionHandler.kt
 * Utility for checking and requesting runtime storage/media permissions.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
object PermissionHandler {
    // Check if we have storage permission (minSdk is 33+, so we only need READ_MEDIA_IMAGES)
    fun hasStoragePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    }
}