package be.rm.secu.tp2.acs

import be.rm.secu.tp2.acs.card.ICardCodeProvider
import be.rm.secu.tp2.core.model.dto.AcsClientRequest
import be.rm.secu.tp2.core.security.Signing
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.security.PublicKey
import java.util.*

class AuthServerRequestHandler(
    private val acsClientPublicKey: PublicKey,
    private val cardCodeProvider: ICardCodeProvider
): IAuthServerRequestHandler {
    override fun handleRequest(request: String): String {
        val (jsonB64, signatureB64) = request.split(".")

        val json = String(Base64.getDecoder().decode(jsonB64))
        val signature = Base64.getDecoder().decode(signatureB64)

        val isOk = Signing.verify(signature, jsonB64.toByteArray(), acsClientPublicKey)

        val acsClientRequest = Json.decodeFromString<AcsClientRequest>(json)
        val hasCardCode = cardCodeProvider.hasCode(acsClientRequest.cardCode)

        return if (isOk == true && hasCardCode) {
            "OK"
        } else {
            "Card code is invalid"
        }
    }
}