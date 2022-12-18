package be.rm.secu.tp2.acq

import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import reactor.core.publisher.Mono
import reactor.netty.tcp.TcpClient
import java.io.File
import java.io.FileInputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

object Application

// Create a TCP client that sends a request to the server on the port 27998
// Secure the client with the truststore provided from the function "getTruststore"
// Start the client and wait for it to terminate
fun main() {
    val client = TcpClient.create().port(27998)
        .secure { sslContextSpec ->
            sslContextSpec.sslContext(getTruststore())
        }
        .connectNow()

    runBlocking {
        launch(Dispatchers.IO) {
            client.outbound()
                .sendString(Mono.just("Hello ACS!"))
                .then()
                .log("OUT")
                .awaitFirstOrNull()
        }
    }

    runBlocking {
        val request = client.inbound()
            .receive()
            .asString()
            .awaitFirst()

        println(request)
    }
}

// Create an SSL context with the truststore provided
// Return the SSL context
fun getTruststore(): SslContext {
    // Extract x509 certificate from /cert/ca.crt
    val caCert = Application::class.java.getResource("/cert/ca.crt")
    val caCertFile = File(caCert.toURI())
    val caCertInputStream = FileInputStream(caCertFile)
    val caCertFactory = CertificateFactory.getInstance("X.509")
    val caCertX509 = caCertFactory.generateCertificate(caCertInputStream) as X509Certificate

    return SslContextBuilder.forClient()
        .trustManager(caCertX509)
        .build()
}
