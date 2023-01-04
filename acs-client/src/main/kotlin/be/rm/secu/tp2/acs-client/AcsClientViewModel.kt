package be.rm.secu.tp2.`acs-client`

import androidx.compose.runtime.mutableStateOf
import be.rm.secu.tp2.core.io.IO
import be.rm.secu.tp2.core.model.dto.AcsClientRequest
import be.rm.secu.tp2.core.model.dto.TotpToken
import be.rm.secu.tp2.core.security.Signing
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.*

class AcsClientViewModel(
    private val acsClient: AcsClient
) {
    val cardNumber = mutableStateOf("")
    val response = mutableStateOf("")
    val startTime = mutableStateOf<LocalDateTime?>(null)
    val endTime = mutableStateOf<LocalDateTime?>(null)

    suspend fun sendToken() {
        val json = Json.encodeToString(AcsClientRequest(cardNumber.value))
        val b64 = Base64.getEncoder().encode(json.toByteArray())

        val privateKey = AcsClientViewModel::class.java.getResourceAsStream("/acs-client.p8")
        val pKey = privateKey?.let { IO.readPrivateKey(it) } ?: throw Exception("Could not load private key")
        val signature = Signing.sign(b64, pKey)

        val responseString = acsClient.sendCardNumber("${String(b64)}.$signature")

        val (totpTokenB64, tokenSignature) = responseString.split(".")
        val totpTokenDecoded = Base64.getDecoder().decode(totpTokenB64)
        val tokenSignatureDecoded = Base64.getDecoder().decode(tokenSignature)

        val totpToken = Json.decodeFromString<TotpToken>(totpTokenDecoded.decodeToString())
        val certificateFile = AcsClientViewModel::class.java.getResourceAsStream("/acs.tp2.secu.rm.be.crt")
        val pubKey = certificateFile?.let { IO.readCertificate(it).publicKey } ?: throw Exception("Could not load ACS public key")

        val verifySignature = Signing.verify(tokenSignatureDecoded, totpTokenB64.toByteArray(), pubKey)

        if (verifySignature == true) {
            response.value = totpToken.password
            startTime.value = Instant.fromEpochMilliseconds(totpToken.startEpoch)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            endTime.value = Instant.fromEpochMilliseconds(totpToken.endEpoch)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        } else {
            response.value = "Signature verification failed"
        }
    }

    fun copyToClipboard() {
        Toolkit.getDefaultToolkit()
            .systemClipboard
            .setContents(StringSelection(response.value), null)
    }
}
