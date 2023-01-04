package be.rm.secu.tp2.acs

import be.rm.secu.tp2.acs.card.HardcodedCardCodeProvider
import be.rm.secu.tp2.core.io.IO
import be.rm.secu.tp2.core.security.SSL
import kotlinx.coroutines.reactive.awaitFirstOrNull
import reactor.core.publisher.Mono
import reactor.netty.DisposableServer
import reactor.netty.tcp.TcpServer

object AuthServer {
    private var server: DisposableServer? = null

    suspend fun run() {
        val keystore = AuthServer::class.java.getResourceAsStream("/cert/acs.keystore.p12")
        val acsClientPublicCertificate = AuthServer::class.java.getResourceAsStream("/cert/acs-client.crt")

        val authServerRequestHandler = AuthServerRequestHandler(
            IO.readCertificate(acsClientPublicCertificate).publicKey,
            HardcodedCardCodeProvider
        )

        server = TcpServer.create()
            .port(27998)
            .secure {
                it.sslContext(SSL.createKeystoreSSLContext(keystore, "acs", "hepl"))
            }
            .handle { inbound, outbound ->
                inbound.receive()
                    .asString()
                    .log("IN")
                    .map(authServerRequestHandler::handleRequest)
                    .flatMap {
                        outbound.sendString(Mono.just(it))
                    }
                    .then()
            }
            .bind()
            .awaitFirstOrNull()
    }

    fun stop() {
        server?.disposeNow()
    }
}