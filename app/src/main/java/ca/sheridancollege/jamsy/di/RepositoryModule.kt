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
import ca.sheridancollege.jamsy.data.repository.ArtistRepositoryImpl
import ca.sheridancollege.jamsy.data.repository.PlaylistRepositoryImpl
import ca.sheridancollege.jamsy.data.repository.SpotifyAuthRepositoryImpl
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
    fun provideSpotifyAuthRepository(): SpotifyAuthRepositoryImpl {
        return SpotifyAuthRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideArtistRepository(): ArtistRepositoryImpl {
        return ArtistRepositoryImpl()
    }

    @Provides
    @Singleton
    fun providePlaylistRepository(): PlaylistRepositoryImpl {
        return PlaylistRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideTrackRepositoryImpl(): TrackRepositoryImpl {
        return TrackRepositoryImpl()
    }
    
    @Provides
    @Singleton
    fun provideTrackRepository(
        trackRepositoryImpl: TrackRepositoryImpl
    ): TrackRepository {
        return trackRepositoryImpl
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
        playlistRepository: PlaylistRepositoryImpl
    ): PlaylistTemplateService {
        return PlaylistTemplateService(playlistRepository)
    }

    @Provides
    @Singleton
    fun provideDiscoveryService(
        trackRepository: TrackRepositoryImpl,
        playlistRepository: PlaylistRepositoryImpl
    ): DiscoveryService {
        return DiscoveryService(trackRepository, playlistRepository)
    }
}

