package be.rm.secu.tp2.core.security

import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.util.*

object Signing {
    fun sign(message: ByteArray, privateKey: PrivateKey): String {
        // Sign the json with the private key
        // Append the signature to the json with a dot
        // Return the signed json
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(message)
        return Base64.getEncoder().encodeToString(signature.sign())
    }

    fun verify(signatureString: ByteArray, data: ByteArray, publicKey: PublicKey): Boolean? {
        // Split the signed json into the json and the signature
        // Verify the signature with the public key
        // If the signature is valid, return the json deserialized
        // Otherwise, return null
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(data)
        return signature.verify(signatureString)
    }
}
