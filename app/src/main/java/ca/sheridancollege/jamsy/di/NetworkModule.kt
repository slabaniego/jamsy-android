package ca.sheridancollege.jamsy.di

import ca.sheridancollege.jamsy.repository.JamsyRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val jamsyRepository = JamsyRepository()
}