package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.PlaylistTemplate
import ca.sheridancollege.jamsy.repository.JamsyRepository
import ca.sheridancollege.jamsy.services.PlaylistTemplateService
import ca.sheridancollege.jamsy.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistTemplateViewModel(
    private val jamsyRepository: JamsyRepository,
    private val playlistTemplateService: PlaylistTemplateService
) : ViewModel() {

    private val _templatesState = MutableStateFlow<Resource<List<PlaylistTemplate>>>(Resource.Loading)
    val templatesState: StateFlow<Resource<List<PlaylistTemplate>>> = _templatesState.asStateFlow()

    fun loadTemplates(authToken: String) {
        viewModelScope.launch {
            _templatesState.value = Resource.Loading
            
            try {
                val result = playlistTemplateService.getDefaultTemplates(authToken)
                result.onSuccess { templates ->
                    _templatesState.value = Resource.Success(templates)
                }.onFailure { exception ->
                    _templatesState.value = Resource.Error(exception.message ?: "Failed to load templates")
                }
            } catch (e: Exception) {
                _templatesState.value = Resource.Error(e.message ?: "Failed to load templates")
            }
        }
    }
}