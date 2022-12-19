package be.rm.secu.tp2.sp.config

import io.ktor.server.application.*
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory

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