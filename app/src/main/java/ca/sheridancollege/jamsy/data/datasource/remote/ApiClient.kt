package ca.sheridancollege.jamsy.data.datasource.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

import java.util.concurrent.TimeUnit

import ca.sheridancollege.jamsy.config.AppConfig
import ca.sheridancollege.jamsy.data.datasource.remote.JamsyApiService

/**
 * Singleton that manages the API client configuration
 */
object ApiClient {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(AppConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(AppConfig.READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(AppConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(AppConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val jamsyApiService: JamsyApiService by lazy {
        retrofit.create(JamsyApiService::class.java)
    }
}