package be.rm.secu.tp2.sp.routes

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.network.tls.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory

fun Application.tcpCall() {
    routing {
        // Create a socket that will send a request to the server on the port 27998
        // Set the request as the body of the HTTP POST request
        // Start the socket and wait for it to terminate
        post("/tcp") {
            val request = call.receive<String>()
            val socket = aSocket(SelectorManager(Dispatchers.IO))
                .tcp()
                .connect("acs.tp2.secu.rm.be", 27998)
                .tls(coroutineContext) {
                    trustManager = getTruststore()
                }

            socket.openWriteChannel(autoFlush = true)
                .writeStringUtf8(request)

            val response = socket.openReadChannel()
                .readUTF8Line()

            call.respond(response ?: "No response")
        }
    }
}

fun getTruststore(): TrustManager {
    // Retrieve the /cert/ca.crt file
    val caCrt = Application::class.java.getResource("/cert/ca.crt")
    val caCrtFile = File(caCrt.toURI())
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
