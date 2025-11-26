package ca.sheridancollege.jamsy.config

object AppConfig {

    const val API_BASE_URL = "http://10.0.2.2:8080/"
    const val SPOTIFY_AUTH_REDIRECT_URI = "jamsy://callback"

    const val CONNECTION_TIMEOUT = 60L
    const val READ_TIMEOUT = 60L
    const val WRITE_TIMEOUT = 60L
}