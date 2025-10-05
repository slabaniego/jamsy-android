package ca.sheridancollege.jamsy.di

import ca.sheridancollege.jamsy.api.ApiClient
import ca.sheridancollege.jamsy.network.JamsyApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJamsyApiService(): JamsyApiService {
        return ApiClient.jamsyApiService
    }
}