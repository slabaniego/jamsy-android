package ca.sheridancollege.jamsy.domain.constants

/**
 * Constants for workout and music discovery configuration.
 * Centralizes all hardcoded values used throughout the application.
 */
object WorkoutConstants {
    /**
     * Cache configuration
     */
    object Cache {
        const val DURATION_MS = 30 * 60 * 1000L // 30 minutes
    }
    
    /**
     * Artist selection configuration
     */
    object Artist {
        const val SHUFFLED_RESULT_COUNT = 20
    }
}

