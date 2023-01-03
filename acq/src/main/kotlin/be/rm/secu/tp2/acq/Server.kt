package be.rm.secu.tp2.acq

import be.rm.secu.tp2.core.security.SSL
import reactor.netty.tcp.TcpServer

// Create a function that creates a server that listens to the 9568 port
// Secure the server with the keystore provided from the function "getKeystore"
// Start the server and wait for it to terminate
fun createServer(port: Int): TcpServer {
    return TcpServer.create()
        .port(port)
        .secure { sslContextSpec ->
            sslContextSpec.sslContext(Application::class.java.getResourceAsStream("/cert/acq.keystore.p12")?.let {
                SSL.createKeystoreSSLContext(
                    it,
                    "acq",
                    "hepl"
                )
            } ?: throw Exception("Could not find acq.keystore.p12"))
        }
}
