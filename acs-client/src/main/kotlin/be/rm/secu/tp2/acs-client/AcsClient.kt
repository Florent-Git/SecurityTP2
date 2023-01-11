package be.rm.secu.tp2.`acs-client`

import be.rm.secu.tp2.core.net.BasicClient
import java.security.cert.Certificate

class AcsClient(
    private val host: String,
    private val port: Int,
    private val caCertificate: Certificate
) {

    suspend fun sendCardNumber(token: String): String {
        val client = BasicClient(
            host,
            port,
            caCertificate
        )

        return client.sendRequest(token)
    }
}
