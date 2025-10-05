package ca.sheridancollege.jamsy.di

import android.content.Context
import ca.sheridancollege.jamsy.auth.AuthManager
import ca.sheridancollege.jamsy.repository.AuthRepository
import ca.sheridancollege.jamsy.repository.JamsyRepository
import ca.sheridancollege.jamsy.repository.TrackRepository
import ca.sheridancollege.jamsy.repository.UserRepository
import ca.sheridancollege.jamsy.services.DiscoveryService
import ca.sheridancollege.jamsy.services.PlaylistTemplateService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
        return TrackRepository(jamsyRepository)
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
