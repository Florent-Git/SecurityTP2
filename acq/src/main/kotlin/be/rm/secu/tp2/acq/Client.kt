package be.rm.secu.tp2.acq

import reactor.netty.tcp.TcpClient

fun createClient(host: String, port: Int): TcpClient = TcpClient.create()
    .host(host)
    .port(port)
    .secure { sslContextSpec ->
        sslContextSpec.sslContext(getTruststore())
    }
