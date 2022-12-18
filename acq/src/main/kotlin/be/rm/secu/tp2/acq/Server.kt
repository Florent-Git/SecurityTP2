package be.rm.secu.tp2.acq

import reactor.netty.tcp.TcpServer

// Create a function that creates a server that listens to the 9568 port
// Secure the server with the keystore provided from the function "getKeystore"
// Start the server and wait for it to terminate
fun createServer(port: Int): TcpServer {
    return TcpServer.create()
        .port(port)
        .secure { sslContextSpec ->
            sslContextSpec.sslContext(getKeystore())
        }
}
