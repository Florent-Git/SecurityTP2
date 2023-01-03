package be.rm.secu.tp2.`acs-client`

import be.rm.secu.tp2.core.security.SSL
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import reactor.core.publisher.Mono
import reactor.netty.tcp.TcpClient

class AcsClient {
    suspend fun sendToken(token: String): String {
        val fileInputStream = AcsClient::class.java.getResourceAsStream("/ca.crt")

        val client = TcpClient.create()
            .host("acs.tp2.secu.rm.be")
            .port(27998)
            .secure { sslContextSpec ->
                sslContextSpec.sslContext(
                    fileInputStream?.let { SSL.createTruststoreSSLContext(it) }
                        ?: throw Exception("Could not load CA certificate")
                )
            }
            .connect()
            .log("acs-client")
            .awaitFirst()

        client.outbound()
            .sendString(Mono.just(token))
            .awaitFirstOrNull()

        val response = client.inbound()
            .receive()
            .asString()
            .log("acs-client")
            .awaitFirstOrNull()

        client.disposeNow()
        fileInputStream?.close()

        return response ?: throw Exception("No response")
    }
}
