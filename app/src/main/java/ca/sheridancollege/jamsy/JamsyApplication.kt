package ca.sheridancollege.jamsy
import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineExceptionHandler

class JamsyApplication : Application(), ImageLoaderFactory {
    companion object {
        private const val TAG = "JamsyApp"

        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "Uncaught exception in coroutine", exception)
        }
    }

    override fun onCreate() {
        super.onCreate()

        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase", e)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .build()
    }
}