package ca.sheridancollege.jamsy.domain.constants

/**
 * Constants for workout and music discovery configuration.
 * Centralizes all hardcoded values used throughout the application.
 */
object WorkoutConstants {
    
    /**
     * Available workout types
     */
    val WORKOUT_TYPES = listOf(
        "Cardio",
        "Strength Training",
        "Yoga",
        "HIIT"
    )
    
    /**
     * Available mood types
     */
    val MOOD_TYPES = listOf(
        "Energetic",
        "Powerful",
        "Calm",
        "Intense"
    )
    
    /**
     * Default mood mapping for each workout type
     */
    val WORKOUT_TO_MOOD_MAP = mapOf(
        "Cardio" to "Energetic",
        "Strength Training" to "Powerful",
        "Yoga" to "Calm",
        "HIIT" to "Intense"
    )
    
    /**
     * Cache configuration
     */
    object Cache {
        const val DURATION_MS = 30 * 60 * 1000L // 30 minutes
        const val API_REQUEST_DELAY_MS = 2000L // 2 seconds delay between requests
        const val PRELOAD_REQUEST_DELAY_MS = 1000L // 1 second delay for preloading
    }
    
    /**
     * Artist selection configuration
     */
    object Artist {
        const val MAX_SELECTION_COUNT = 5
        const val SHUFFLED_RESULT_COUNT = 20
    }
    
    /**
     * Get default mood for a workout type.
     * 
     * @param workout The workout type
     * @return The corresponding mood, defaults to "Energetic"
     */
    fun getMoodForWorkout(workout: String): String {
        return WORKOUT_TO_MOOD_MAP[workout] ?: "Energetic"
    }
}

