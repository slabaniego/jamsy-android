package ca.sheridancollege.jamsy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import ca.sheridancollege.jamsy.domain.models.Track
import ca.sheridancollege.jamsy.domain.usecases.HandleTrackActionUseCase
import ca.sheridancollege.jamsy.util.Resource

/**
 * SwipeViewModel - Manages swipe card state and user interactions
 * Follows Single Responsibility Principle: handles ONLY swipe-related logic
 * Uses Dependency Injection for loose coupling
 */
@HiltViewModel
class SwipeViewModel @Inject constructor(
    private val handleTrackActionUseCase: HandleTrackActionUseCase
) : ViewModel() {

    // State for current card
    private val _currentCardIndex = MutableStateFlow(0)
    val currentCardIndex: StateFlow<Int> = _currentCardIndex.asStateFlow()

    // State for all cards
    private val _cards = MutableStateFlow<List<Track>>(emptyList())
    val cards: StateFlow<List<Track>> = _cards.asStateFlow()

    // State for liked tracks
    private val _likedTracks = MutableStateFlow<List<Track>>(emptyList())
    val likedTracks: StateFlow<List<Track>> = _likedTracks.asStateFlow()

    // State for disliked tracks
    private val _dislikedTracks = MutableStateFlow<List<Track>>(emptyList())
    val dislikedTracks: StateFlow<List<Track>> = _dislikedTracks.asStateFlow()

    // Loading state
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Initialize cards for swiping
     * @param initialCards List of tracks to swipe through
     */
    fun setCards(initialCards: List<Track>) {
        _cards.value = initialCards
        _currentCardIndex.value = 0
        _likedTracks.value = emptyList()
        _dislikedTracks.value = emptyList()
        _error.value = null
    }

    /**
     * Get current card being swiped
     */
    fun getCurrentCard(): Track? {
        val index = _currentCardIndex.value
        val cards = _cards.value
        return if (index < cards.size) cards[index] else null
    }

    /**
     * Check if there are more cards to swipe
     */
    fun hasMoreCards(): Boolean {
        return _currentCardIndex.value < _cards.value.size - 1
    }

    /**
     * Handle like action on current card
     * @param track The track that was liked
     * @param authToken Authentication token for API calls
     */
    fun handleLike(track: Track, authToken: String) {
        handleTrackAction(track, "like", authToken)
    }

    /**
     * Handle dislike action on current card
     * @param track The track that was disliked
     * @param authToken Authentication token for API calls
     */
    fun handleDislike(track: Track, authToken: String) {
        handleTrackAction(track, "dislike", authToken)
    }

    /**
     * Generic handler for track actions (like/dislike)
     */
    private fun handleTrackAction(track: Track, action: String, authToken: String) {
        viewModelScope.launch {
            try {
                _isProcessing.value = true
                _error.value = null

                // Call use case to handle the action
                val result = handleTrackActionUseCase(track, action, authToken)

                when (result) {
                    is Resource.Success -> {
                        // Add to appropriate list
                        when (action) {
                            "like" -> addLikedTrack(track)
                            "dislike" -> addDislikedTrack(track)
                        }
                        moveToNextCard()
                    }
                    is Resource.Error -> {
                        _error.value = result.message ?: "Failed to process action"
                        moveToNextCard() // Still move to next card even on error
                    }
                    is Resource.Loading -> { /* No-op */ }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred"
                moveToNextCard() // Still move to next card even on error
            } finally {
                _isProcessing.value = false
            }
        }
    }

    /**
     * Add track to liked list
     */
    private fun addLikedTrack(track: Track) {
        val current = _likedTracks.value.toMutableList()
        // Prevent duplicates
        if (!current.any { it.id == track.id }) {
            current.add(track)
            _likedTracks.value = current
        }
    }

    /**
     * Add track to disliked list
     */
    private fun addDislikedTrack(track: Track) {
        val current = _dislikedTracks.value.toMutableList()
        // Prevent duplicates
        if (!current.any { it.id == track.id }) {
            current.add(track)
            _dislikedTracks.value = current
        }
    }

    /**
     * Move to next card
     */
    private fun moveToNextCard() {
        val currentIndex = _currentCardIndex.value
        val totalCards = _cards.value.size
        
        if (currentIndex < totalCards - 1) {
            _currentCardIndex.value = currentIndex + 1
        }
    }

    /**
     * Reset all state
     */
    fun reset() {
        _currentCardIndex.value = 0
        _likedTracks.value = emptyList()
        _dislikedTracks.value = emptyList()
        _isProcessing.value = false
        _error.value = null
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Get statistics about swiping session
     */
    fun getSessionStats(): SessionStats {
        val total = _cards.value.size
        val liked = _likedTracks.value.size
        val disliked = _dislikedTracks.value.size
        val remaining = total - _currentCardIndex.value

        return SessionStats(
            totalCards = total,
            likedCount = liked,
            dislikedCount = disliked,
            remainingCards = remaining,
            progressPercentage = if (total > 0) ((_currentCardIndex.value + 1) * 100) / total else 0
        )
    }
}

/**
 * Data class for session statistics
 * Encapsulates session metrics
 */
data class SessionStats(
    val totalCards: Int = 0,
    val likedCount: Int = 0,
    val dislikedCount: Int = 0,
    val remainingCards: Int = 0,
    val progressPercentage: Int = 0
)
