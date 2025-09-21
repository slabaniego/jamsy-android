package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ca.sheridancollege.jamsy.di.NetworkModule
import ca.sheridancollege.jamsy.repository.AuthRepository
import ca.sheridancollege.jamsy.repository.JamsyRepository
import ca.sheridancollege.jamsy.repository.TrackRepository
import ca.sheridancollege.jamsy.repository.UserRepository
import ca.sheridancollege.jamsy.services.PlaylistTemplateService
import ca.sheridancollege.jamsy.services.DiscoveryService


class ViewModelFactory : ViewModelProvider.Factory {

    private val authRepository = AuthRepository()
    private val jamsyRepository = JamsyRepository()
    private val trackRepository = TrackRepository(jamsyRepository)
    private val userRepository = UserRepository()
    private val playlistTemplateService = PlaylistTemplateService(jamsyRepository)
    private val discoveryService = DiscoveryService(jamsyRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(trackRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel() as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(
                    authRepository = AuthRepository(),
                    jamsyRepository = NetworkModule.jamsyRepository
                ) as T
            }
            modelClass.isAssignableFrom(TrackListViewModel::class.java) -> {
                TrackListViewModel(trackRepository) as T
            }
            modelClass.isAssignableFrom(PlaylistTemplateViewModel::class.java) -> {
                PlaylistTemplateViewModel(jamsyRepository, playlistTemplateService) as T
            }
            modelClass.isAssignableFrom(ArtistSelectionViewModel::class.java) -> {
                ArtistSelectionViewModel(jamsyRepository) as T
            }
            modelClass.isAssignableFrom(DiscoveryViewModel::class.java) -> {
                DiscoveryViewModel(jamsyRepository) as T
            }
            modelClass.isAssignableFrom(LikedTracksViewModel::class.java) -> {
                LikedTracksViewModel(jamsyRepository) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(NetworkModule.jamsyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}