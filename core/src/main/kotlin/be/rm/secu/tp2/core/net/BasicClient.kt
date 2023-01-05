package be.rm.secu.tp2.core.net

import be.rm.secu.tp2.core.security.SSL
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import reactor.core.publisher.Mono
import reactor.netty.tcp.TcpClient
import java.security.cert.Certificate
import java.security.cert.X509Certificate

class BasicClient(
    private val host: String,
    private val port: Int,
    private val caCertificate: Certificate
) {
    private val client by lazy {
        TcpClient.create()
            .host(host)
            .port(port)
            .secure { sslContextSpec ->
                sslContextSpec.sslContext(
                    SSL.createCertificateSSLContext(caCertificate as X509Certificate)
                )
            }
            .connect()
            .log("client")
    }

    suspend fun sendRequest(request: String): String {
        val connection = client
            .awaitFirst()

        connection.outbound()
            .sendString(Mono.just(request))
            .awaitFirstOrNull()

        val response = connection.inbound()
            .receive()
            .asString()
            .log("acs-client")
            .awaitFirstOrNull()

        connection.disposeNow()

        return response ?: throw Exception("No response")
    }
}