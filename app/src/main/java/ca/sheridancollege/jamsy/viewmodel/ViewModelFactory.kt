package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ca.sheridancollege.jamsy.api.TrackApiClient
import ca.sheridancollege.jamsy.repository.AuthRepository
import ca.sheridancollege.jamsy.repository.TrackRepository
import ca.sheridancollege.jamsy.repository.UserRepository


class ViewModelFactory : ViewModelProvider.Factory {


    private val authRepository = AuthRepository()
    private val trackRepository = TrackRepository(TrackApiClient.instance)
    private val userRepository = UserRepository()

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
                AuthViewModel() as T
            }
            modelClass.isAssignableFrom(TrackListViewModel::class.java) -> {
                TrackListViewModel(trackRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}