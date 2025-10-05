package ca.sheridancollege.jamsy.util

import android.annotation.SuppressLint

/**
 * Formats duration in seconds to a human-readable string (MM:SS)
 */
@SuppressLint("DefaultLocale")
fun formatDuration(durationInSeconds: Long): String {
    val minutes = durationInSeconds / 60
    val seconds = durationInSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

/**
 * Overloaded function to support Int durations
 */
fun formatDuration(durationInSeconds: Int): String {
    return formatDuration(durationInSeconds.toLong())
}