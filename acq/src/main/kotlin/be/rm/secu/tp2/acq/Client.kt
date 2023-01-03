package be.rm.secu.tp2.acq

import be.rm.secu.tp2.core.security.SSL
import reactor.netty.tcp.TcpClient

fun createClient(host: String, port: Int): TcpClient = TcpClient.create()
    .host(host)
    .port(port)
    .secure { sslContextSpec ->
        sslContextSpec.sslContext(
            SSL.createTruststoreSSLContext(
                Application::class.java.getResourceAsStream("/cert/ca.crt") ?: throw Exception("Could not find ca.crt")
            )
        )
    }
