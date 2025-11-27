/*
 * JamsyApplication.kt
 * Hilt-enabled Application class configuring Firebase and Coil image loading.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class JamsyApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
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