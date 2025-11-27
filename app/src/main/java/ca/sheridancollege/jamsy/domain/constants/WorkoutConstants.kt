/*
 * WorkoutConstants.kt
 * Constants for workout and music discovery configuration.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.domain.constants
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

