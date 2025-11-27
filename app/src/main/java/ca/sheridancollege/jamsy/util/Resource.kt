/*
 * Resource.kt
 * Simple sealed class wrapper for representing loading, success, and error states.
 *
 * Author: Iurii Manastyrskyi
 */
package ca.sheridancollege.jamsy.util

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}