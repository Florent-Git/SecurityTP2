package be.rm.secu.tp2.acs

import reactor.core.publisher.Mono
import reactor.netty.tcp.SslProvider.SslContextSpec
import reactor.netty.tcp.TcpServer
import java.io.File
import java.io.FileInputStream
import java.security.PrivateKey
import java.security.cert.X509Certificate

object Application

fun main() {
    // Retrieve /certs/acq.p12 from the classpath
    val keystore = Application::class.java.getResource("/cert/acs.keystore.p12")

    // Create a TCP server that will listen on port 27998
    // Secure the server with the keystore provided
    // Start the server and wait for it to terminate
    TcpServer.create()
        .port(27998)
        .secure { sslContextSpec -> sslContextSpec.sslContext(
            FileInputStream(File(keystore.toURI())),
            "hepl"
        )}
        .handle { inbound, outbound ->
            inbound.receive()
                .asString()
                .log("IN")
                .map {
                    "Echo: $it"
                }
                .flatMap {
                    outbound.sendString(Mono.just(it))
                }
                .then()
        }
        .bindNow()
        .onDispose()
        .block()
}

// Create an SSL context with a keystore and a password from the parameters
// Return the SSL context
fun SslContextSpec.sslContext(
    keystoreFile: FileInputStream, password: String
) {
    val keyStore = java.security.KeyStore.getInstance("PKCS12")
    keyStore.load(keystoreFile, password.toCharArray())
    this.sslContext(
        io.netty.handler.ssl.SslContextBuilder.forServer(
            keyStore.getKey("acs", password.toCharArray()) as PrivateKey,
            keyStore.getCertificate("acs") as X509Certificate,
        ).build()
    )
}

