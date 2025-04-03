package ca.sheridancollege.jamsy.network
import com.google.gson.*
import java.lang.reflect.Type
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException

/**
 * A Gson type adapter that can handle both JSON objects and string responses
 */
class SafeResponseTypeAdapter<T>(private val type: Class<T>) : JsonDeserializer<T> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): T? {
        return try {
            // Try normal deserialization
            GsonBuilder().create().fromJson(json, type)
        } catch (e: JsonSyntaxException) {
            // If the response is a string (error message), return null
            // or create an error response object depending on your model
            println("API returned non-JSON response: ${json.asString}")
            null
        }
    }
}

/**
 * Converter factory for handling string responses
 */
class StringConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (type == String::class.java) {
            Converter<ResponseBody, String> { it.string() }
        } else {
            // Try to parse as JSON, if fails, get the string and wrap it
            Converter<ResponseBody, Any> {
                val bodyString = it.string()

                // Check if the response starts with HTML tags
                if (bodyString.trim().startsWith("<!DOCTYPE html>") ||
                    bodyString.trim().startsWith("<html")) {
                    throw IOException("Server returned HTML instead of JSON. Authentication may be required.")
                }

                try {
                    // Use Gson to properly parse the JSON to the expected type
                    val gson = GsonBuilder().setLenient().create()
                    gson.fromJson(bodyString, type)
                } catch (e: JsonSyntaxException) {

                    // Only return the raw string if we're expecting a String
                    if (type == String::class.java) {
                        bodyString
                    } else {
                        throw IOException("Server returned invalid JSON: ${e.message}")

                    }
                }
            }
        }
    }
}