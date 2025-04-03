package ca.sheridancollege.jamsy.config

object AppConfig {

    const val API_BASE_URL = "http://10.0.2.2:8080/"
    const val SPOTIFY_AUTH_REDIRECT_URI = "jamsy://callback"

    const val CONNECTION_TIMEOUT = 60L
    const val READ_TIMEOUT = 60L
    const val WRITE_TIMEOUT = 60L

    const val PREF_ACCESS_TOKEN = "access_token"
    const val PREF_REFRESH_TOKEN = "refresh_token"
    const val PREF_TOKEN_EXPIRES_AT = "token_expires_at"
    const val PREF_FIREBASE_TOKEN = "firebase_token"
}