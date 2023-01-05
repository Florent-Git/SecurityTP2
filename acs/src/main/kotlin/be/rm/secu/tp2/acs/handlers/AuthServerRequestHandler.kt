package be.rm.secu.tp2.acs.handlers

import be.rm.secu.tp2.acs.card.ICardCodeProvider
import be.rm.secu.tp2.acs.rsaotp.TimeBasedSignatureOneTimePasswordConfig
import be.rm.secu.tp2.acs.rsaotp.TimeBasedSignatureOneTimePasswordGenerator
import be.rm.secu.tp2.core.io.IO
import be.rm.secu.tp2.core.model.dto.AcsClientRequest
import be.rm.secu.tp2.core.model.dto.TotpToken
import be.rm.secu.tp2.core.net.IRequestHandler
import be.rm.secu.tp2.core.security.Signing
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

class AuthServerRequestHandler(
    private val acsClientPublicKey: PublicKey,
    private val cardCodeProvider: ICardCodeProvider,
    private val totpConfig: TimeBasedSignatureOneTimePasswordConfig
): IRequestHandler<String, String> {
    private val acsPrivateKey by lazy {
        val keystoreStream = AuthServerRequestHandler::class.java.getResourceAsStream("/cert/acs.keystore.p12")
        val keystore = keystoreStream?.let { IO.readKeystore(it, "hepl") } ?: throw Exception("Keystore not found")
        keystore.getKey("acs", "hepl".toCharArray()) as PrivateKey
    }

    private val generator by lazy {
        TimeBasedSignatureOneTimePasswordGenerator(
            acsPrivateKey,
            totpConfig
        )
    }

    override fun handleRequest(request: String): String {
        val (jsonB64, signatureB64) = request.split(".")

        val json = String(Base64.getDecoder().decode(jsonB64))
        val signature = Base64.getDecoder().decode(signatureB64)

        val isOk = Signing.verify(signature, jsonB64.toByteArray(), acsClientPublicKey)

        val acsClientRequest = Json.decodeFromString<AcsClientRequest>(json)
        val hasCardCode = cardCodeProvider.hasCode(acsClientRequest.cardCode)

        return if (isOk == true && hasCardCode) {
            val totp = generator.generate()
            val counter = generator.counter()

            val token = TotpToken(
                password = totp,
                startEpoch = generator.timeslotStart(counter),
                endEpoch = generator.timeslotStart(counter + 1) - 1
            )

            val tokenJson = Json.encodeToString(token)
            val tokenJsonB64 = Base64.getEncoder().encodeToString(tokenJson.toByteArray())
            val tokenSignature = Signing.sign(tokenJsonB64.toByteArray(), acsPrivateKey)

            "$tokenJsonB64.$tokenSignature"
        } else {
            "Card code is invalid"
        }
    }
}