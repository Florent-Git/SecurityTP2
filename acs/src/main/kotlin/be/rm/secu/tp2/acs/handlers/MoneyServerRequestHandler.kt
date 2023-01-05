package be.rm.secu.tp2.acs.handlers

import be.rm.secu.tp2.acs.rsaotp.TimeBasedSignatureOneTimePasswordConfig
import be.rm.secu.tp2.acs.rsaotp.TimeBasedSignatureOneTimePasswordGenerator
import be.rm.secu.tp2.core.model.dto.TotpToken
import be.rm.secu.tp2.core.net.IRequestHandler
import be.rm.secu.tp2.core.security.Signing
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.security.KeyStore
import java.security.PrivateKey
import java.util.*

class MoneyServerRequestHandler (
    private val acsKeyStore: KeyStore,
    totpConfig: TimeBasedSignatureOneTimePasswordConfig
): IRequestHandler<String, String> {
    private val acsPublicKey by lazy {
        acsKeyStore.getCertificate("acs").publicKey
    }

    private val acsPrivateKey by lazy {
        acsKeyStore.getKey("acs", "hepl".toCharArray()) as PrivateKey
    }

    private val generator by lazy {
        TimeBasedSignatureOneTimePasswordGenerator(
            acsPrivateKey,
            totpConfig
        )
    }

    override fun handleRequest(request: String): String {
        val (payload64, signature64) = request.split(".")
        val payload = Base64.getDecoder().decode(payload64)
        val signature = Base64.getDecoder().decode(signature64)

        val isOk = Signing.verify(signature, payload64.toByteArray(), acsPublicKey)

        val totpToken = Json.decodeFromString<TotpToken>(payload.decodeToString())
        val isTokenValid = generator.isValid(totpToken.password)

        return if (isOk == true && isTokenValid) {
            "ACK"
        } else {
            "NACK"
        }
    }
}