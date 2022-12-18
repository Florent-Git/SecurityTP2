package be.rm.secu.tp2.sp.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.netty.handler.ssl.SslContextBuilder
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import reactor.core.publisher.Mono
import reactor.netty.tcp.TcpClient
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory

fun Application.tcpCall() {
    routing {
        post("/tcp") {
            // Receive the request from the HTTP POST request as a form parameter named "request"
            val request = call.receiveParameters()["request"] ?: "No request"

            // Create a TCP socket that will send a request to the server on the port 27998
            // Create the client with reactor-netty
            val client = TcpClient.create()
                .host("acq.tp2.secu.rm.be")
                .port(9276)
                .secure {
                    it.sslContext(
                        SslContextBuilder
                            .forClient()
                            .trustManager(getTruststore())
                            .build()
                    )
                }
                .connectNow()

            // Send the request to the server
            client.outbound()
                .sendString(Mono.just(request))
                .then()
                .log("OUT")
                .awaitFirstOrNull()

            // Receive the response from the server
            val response = client.inbound()
                .receive()
                .asString()
                .awaitFirst()

            // Send the response to the client
            call.respond(response)
        }
    }
}

fun getTruststore(): TrustManager {
    // Retrieve the /cert/ca.crt file
    val caCrt = Application::class.java.getResource("/cert/ca.crt")
    val caCrtFile = caCrt?.toURI()?.let { File(it) } ?: throw Exception("Could not find ca.crt")
    val caCrtInputStream = FileInputStream(caCrtFile)

    // Create a CertificateFactory with the certificate provided
    val certificateFactory = CertificateFactory.getInstance("X.509")
    val certificate = certificateFactory.generateCertificate(caCrtInputStream) as X509Certificate

    // Add the certificate to the truststore
    val trustStore = KeyStore.getInstance(KeyStore.getDefaultType())
    trustStore.load(null, null)
    trustStore.setCertificateEntry("ca", certificate)

    // Create a TruststoreManager with the truststore provided
    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(trustStore)

    return trustManagerFactory.trustManagers[0]
}
