package be.rm.secu.tp2.core.security

import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import java.io.InputStream
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

object SSL {
    fun createTruststoreSSLContext(inputStream: InputStream): SslContext {
        val caCertFactory = CertificateFactory.getInstance("X.509")
        val caCertX509 = caCertFactory.generateCertificate(inputStream) as X509Certificate

        return SslContextBuilder.forClient()
            .trustManager(caCertX509)
            .build()
    }

    fun createCertificateSSLContext(certificate: X509Certificate): SslContext {
        return SslContextBuilder.forClient()
            .trustManager(certificate)
            .build()
    }

    fun createKeystoreSSLContext(keyStore: KeyStore, alias: String, password: String): SslContext {
        return SslContextBuilder.forServer(
            keyStore.getKey(alias, password.toCharArray()) as PrivateKey,
            keyStore.getCertificate(alias) as X509Certificate,
        ).build()
    }
}