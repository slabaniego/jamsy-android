package ca.sheridancollege.jamsy.di

import ca.sheridancollege.jamsy.api.TrackApiService
import ca.sheridancollege.jamsy.repository.TrackRepository
import ca.sheridancollege.jamsy.api.TrackApiClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val trackService: TrackApiService = retrofit.create(TrackApiService::class.java)
    val trackRepository = TrackRepository(TrackApiClient.instance)
}