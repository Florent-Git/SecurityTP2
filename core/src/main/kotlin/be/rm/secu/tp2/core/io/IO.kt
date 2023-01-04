package be.rm.secu.tp2.core.io

import java.io.InputStream
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.spec.PKCS8EncodedKeySpec

object IO {
    fun readPrivateKey(inputStream: InputStream): PrivateKey {
        // Read the private key from the input stream
        // Return the private key
        return KeyFactory.getInstance("RSA")
            .generatePrivate(PKCS8EncodedKeySpec(inputStream.readAllBytes()))
    }

    fun readPublicKey(inputStream: InputStream): PublicKey {
        // Read the public key from the input stream
        // Return the public key
        return KeyFactory.getInstance("RSA")
            .generatePublic(PKCS8EncodedKeySpec(inputStream.readAllBytes()))
    }

    fun readCertificate(inputStream: InputStream): Certificate {
        val caCertFactory = CertificateFactory.getInstance("X.509")
        return caCertFactory.generateCertificate(inputStream)
    }
}