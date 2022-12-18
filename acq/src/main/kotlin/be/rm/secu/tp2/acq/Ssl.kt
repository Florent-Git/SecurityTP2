package be.rm.secu.tp2.acq

import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import java.io.File
import java.io.FileInputStream
import java.security.PrivateKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


// Create an SSL context with the truststore provided
// Return the SSL context
fun getTruststore(): SslContext {
    // Extract x509 certificate from /cert/ca.crt
    val caCert = Application::class.java.getResource("/cert/ca.crt")
    val caCertFile = caCert?.toURI()?.let { File(it) } ?: throw Exception("Could not find ca.crt")
    val caCertInputStream = FileInputStream(caCertFile)
    val caCertFactory = CertificateFactory.getInstance("X.509")
    val caCertX509 = caCertFactory.generateCertificate(caCertInputStream) as X509Certificate

    return SslContextBuilder.forClient()
        .trustManager(caCertX509)
        .build()
}

// Create an SSL context with the keystore provided from /cert/acq.keystore.p12
// Return the SSL context
fun getKeystore(): SslContext {
    // Retrieve /certs/acq.p12 from the classpath
    val keystore = Application::class.java.getResource("/cert/acq.keystore.p12")

    val keyStore = java.security.KeyStore.getInstance("PKCS12")
    keyStore.load(FileInputStream(File(keystore.toURI())), "hepl".toCharArray())
    return SslContextBuilder.forServer(
        keyStore.getKey("acq", "hepl".toCharArray()) as PrivateKey,
        keyStore.getCertificate("acq") as X509Certificate,
    ).build()
}
