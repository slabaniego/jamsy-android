package ca.sheridancollege.jamsy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

import ca.sheridancollege.jamsy.data.PlaylistTemplateService
import ca.sheridancollege.jamsy.data.repository.JamsyRepository
import ca.sheridancollege.jamsy.domain.models.PlaylistTemplate
import ca.sheridancollege.jamsy.util.Resource

@HiltViewModel
class PlaylistTemplateViewModel @Inject constructor(
    private val jamsyRepository: JamsyRepository,
    private val playlistTemplateService: PlaylistTemplateService
) : ViewModel() {

    private val _templatesState = MutableStateFlow<Resource<List<PlaylistTemplate>>>(Resource.Loading)
    val templatesState: StateFlow<Resource<List<PlaylistTemplate>>> = _templatesState.asStateFlow()

    fun loadTemplates(authToken: String) {
        viewModelScope.launch {
            _templatesState.value = Resource.Loading
            
            try {
                println("PlaylistTemplateViewModel: Loading templates with authToken: ${if (authToken.isBlank()) "EMPTY" else "PRESENT"}")
                val result = playlistTemplateService.getDefaultTemplates(authToken)
                result.onSuccess { templates ->
                    println("PlaylistTemplateViewModel: Successfully loaded ${templates.size} templates")
                    _templatesState.value = Resource.Success(templates)
                }.onFailure { exception ->
                    println("PlaylistTemplateViewModel: Error loading templates: ${exception.message}")
                    _templatesState.value = Resource.Error(exception.message ?: "Failed to load templates")
                }
            } catch (e: Exception) {
                println("PlaylistTemplateViewModel: Exception loading templates: ${e.message}")
                _templatesState.value = Resource.Error(e.message ?: "Failed to load templates")
            }
        }
    }
}