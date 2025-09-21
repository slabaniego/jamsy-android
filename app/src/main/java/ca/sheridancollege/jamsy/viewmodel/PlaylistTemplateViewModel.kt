package ca.sheridancollege.jamsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sheridancollege.jamsy.model.PlaylistTemplate
import ca.sheridancollege.jamsy.repository.JamsyRepository
import ca.sheridancollege.jamsy.services.PlaylistTemplateService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistTemplateViewModel(
    private val jamsyRepository: JamsyRepository,
    private val playlistTemplateService: PlaylistTemplateService
) : ViewModel() {

    private val _templates = MutableStateFlow<List<PlaylistTemplate>>(emptyList())
    val templates: StateFlow<List<PlaylistTemplate>> = _templates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadTemplates() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = playlistTemplateService.getDefaultTemplates()
                _templates.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load templates"
            } finally {
                _isLoading.value = false
            }
        }
    }
}