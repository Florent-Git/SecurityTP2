package be.rm.secu.tp2.core.model.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.Base64

data class Token<T>(
    val header: Header,
    val payload: T,
    val signature: ByteArray
) {
    companion object {
        inline fun <reified T> fromTokenString(tokenString: String): Token<T> {
            val (header, payload, signature) = tokenString.split(".")

            val decodedHeaderJson = Base64.getDecoder().decode(header)
            val decodedPayloadJson = Base64.getDecoder().decode(payload)

            val decodedHeader = Json.decodeFromString<Header>(decodedHeaderJson.decodeToString())
            val decodedPayload = Json.decodeFromString<T>(decodedPayloadJson.decodeToString())

            return Token(
                header = decodedHeader,
                payload = decodedPayload,
                signature = Base64.getDecoder().decode(signature)
            )
        }
    }

    fun toTokenString() {
        val header64 = header.toBase64String()
        val payload64 = payload.toBase64String()

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Token<*>

        if (header != other.header) return false
        if (payload != other.payload) return false
        if (!signature.contentEquals(other.signature)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = header.hashCode()
        result = 31 * result + (payload?.hashCode() ?: 0)
        result = 31 * result + signature.contentHashCode()
        return result
    }
}
