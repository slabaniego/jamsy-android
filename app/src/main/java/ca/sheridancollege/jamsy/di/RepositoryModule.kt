package ca.sheridancollege.jamsy.di

import android.content.Context

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

import ca.sheridancollege.jamsy.data.AuthManager
import ca.sheridancollege.jamsy.data.DiscoveryService
import ca.sheridancollege.jamsy.data.PlaylistTemplateService
import ca.sheridancollege.jamsy.data.repository.AuthRepository
import ca.sheridancollege.jamsy.data.repository.JamsyRepository
import ca.sheridancollege.jamsy.data.repository.TrackRepository as TrackRepositoryImpl
import ca.sheridancollege.jamsy.data.repository.UserRepository
import ca.sheridancollege.jamsy.domain.repository.TrackRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepository(context)
    }

    @Provides
    @Singleton
    fun provideJamsyRepository(): JamsyRepository {
        return JamsyRepository()
    }

    @Provides
    @Singleton
    fun provideTrackRepository(
        jamsyRepository: JamsyRepository
    ): TrackRepository {
        return TrackRepositoryImpl(jamsyRepository)
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

    @Provides
    @Singleton
    fun provideAuthManager(
        @ApplicationContext context: Context
    ): AuthManager {
        return AuthManager(context)
    }

    @Provides
    @Singleton
    fun providePlaylistTemplateService(
        jamsyRepository: JamsyRepository
    ): PlaylistTemplateService {
        return PlaylistTemplateService(jamsyRepository)
    }

    @Provides
    @Singleton
    fun provideDiscoveryService(
        jamsyRepository: JamsyRepository
    ): DiscoveryService {
        return DiscoveryService(jamsyRepository)
    }
}
