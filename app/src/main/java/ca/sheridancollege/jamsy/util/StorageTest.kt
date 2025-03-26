package ca.sheridancollege.jamsy.util

import android.content.Context
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

object StorageTest {
    private const val TAG = "StorageTest"

    suspend fun testStorageAccess(): Boolean {
        return try {
            val storage = FirebaseStorage.getInstance()
            val testRef = storage.reference.child("test.txt")

            // Just list items to test access
            val list = storage.reference.listAll().await()
            Log.d(TAG, "Listed items: ${list.items.size}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Storage test failed", e)
            false
        }
    }

    suspend fun testFileUpload(context: Context): Boolean {
        return try {
            val tempFile = File(context.cacheDir, "test.txt")
            tempFile.writeText("Test content")

            val storage = FirebaseStorage.getInstance()
            val testRef = storage.reference.child("test-uploads/test.txt")

            withContext(Dispatchers.IO) {
                testRef.putFile(android.net.Uri.fromFile(tempFile)).await()
            }

            Log.d(TAG, "Test file upload successful")
            tempFile.delete()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Test file upload failed", e)
            false
        }
    }
}