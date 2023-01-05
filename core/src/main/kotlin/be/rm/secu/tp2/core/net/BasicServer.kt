package be.rm.secu.tp2.core.net

import be.rm.secu.tp2.core.security.SSL
import kotlinx.coroutines.reactive.awaitFirstOrNull
import reactor.core.publisher.Mono
import reactor.netty.DisposableServer
import reactor.netty.tcp.TcpServer
import java.security.KeyStore

class BasicServer(
    private val port: Int,
    private val keyStore: KeyStore,
    private val alias: String,
    private val password: String,
    private val requestHandler: IRequestHandler<String, String>
) {
    private var server: DisposableServer? = null

    suspend fun run() {
        server = TcpServer.create()
            .port(port)
            .secure {
                it.sslContext(SSL.createKeystoreSSLContext(keyStore, alias, password))
            }
            .handle { inbound, outbound ->
                inbound.receive()
                    .asString()
                    .log("IN")
                    .map(requestHandler::handleRequest)
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