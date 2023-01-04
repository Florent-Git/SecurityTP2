package be.rm.secu.tp2.`acs-client`

import androidx.compose.runtime.mutableStateOf
import be.rm.secu.tp2.core.io.IO
import be.rm.secu.tp2.core.model.dto.AcsClientRequest
import be.rm.secu.tp2.core.security.Signing
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.*

class AcsClientViewModel(
    private val acsClient: AcsClient
) {
    val token = mutableStateOf("")
    val response = mutableStateOf("")

    suspend fun sendToken() {
        val json = Json.encodeToString(AcsClientRequest(token.value))
        val b64 = Base64.getEncoder().encode(json.toByteArray())

        val privateKey = AcsClientViewModel::class.java.getResourceAsStream("/acs-client.p8")
        val pKey = privateKey?.let { IO.readPrivateKey(it) } ?: throw Exception("Could not load private key")
        val signature = Signing.sign(b64, pKey)

        response.value = acsClient.sendToken(
            "${String(b64)}.$signature"
        )
    }

    fun copyToClipboard() {
        Toolkit.getDefaultToolkit()
            .systemClipboard
            .setContents(StringSelection(response.value), null)
    }
}
