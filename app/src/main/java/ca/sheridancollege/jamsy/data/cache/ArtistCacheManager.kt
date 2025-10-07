package ca.sheridancollege.jamsy.data.cache

import ca.sheridancollege.jamsy.domain.constants.WorkoutConstants
import ca.sheridancollege.jamsy.domain.models.Artist
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages caching of artists by workout type.
 * Thread-safe cache implementation with expiration support.
 */
class ArtistCacheManager {
    
    private val cache = ConcurrentHashMap<String, List<Artist>>()
    private var cacheTimestamp: Long = 0
    
    /**
     * Check if the cache is valid (not expired).
     * 
     * @return true if cache is still valid, false otherwise
     */
    fun isCacheValid(): Boolean {
        return (System.currentTimeMillis() - cacheTimestamp) < WorkoutConstants.Cache.DURATION_MS
    }
    
    /**
     * Get cached artists for a workout type.
     * 
     * @param workout The workout type
     * @return List of cached artists or null if not found
     */
    fun get(workout: String): List<Artist>? {
        return if (isCacheValid()) {
            cache[workout]
        } else {
            null
        }
    }
    
    /**
     * Store artists in cache for a workout type.
     * 
     * @param workout The workout type
     * @param artists List of artists to cache
     */
    fun put(workout: String, artists: List<Artist>) {
        cache[workout] = artists
        updateTimestamp()
    }
    
    /**
     * Clear the entire cache.
     */
    fun clear() {
        cache.clear()
        cacheTimestamp = 0
    }
    
    /**
     * Update the cache timestamp to current time.
     */
    private fun updateTimestamp() {
        cacheTimestamp = System.currentTimeMillis()
    }
    
    /**
     * Get all cached workout types.
     * 
     * @return Set of workout type keys
     */
    fun getCachedWorkouts(): Set<String> {
        return if (isCacheValid()) {
            cache.keys
        } else {
            emptySet()
        }
    }
    
    /**
     * Check if a specific workout has cached data.
     * 
     * @param workout The workout type
     * @return true if workout has valid cached data
     */
    fun hasCachedData(workout: String): Boolean {
        return isCacheValid() && cache.containsKey(workout)
    }
}

