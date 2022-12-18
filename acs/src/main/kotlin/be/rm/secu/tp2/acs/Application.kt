package be.rm.secu.tp2.acs

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import reactor.core.publisher.Mono
import reactor.netty.tcp.SslProvider.SslContextSpec
import reactor.netty.tcp.TcpServer
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.security.*
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import kotlin.collections.ArrayList
import kotlin.experimental.and
import kotlin.random.Random

object Application

@Serializable
data class Header(
  val alg: String
);

@Serializable
data class Token(
  val token: Int
);

val tokens = ArrayList<ByteArray>()
const val PORT_AUTH = 27998
const val PORT_MONEY = 27999
fun main() {
    // Retrieve /certs/acq.p12 from the classpath
    val keystore = Application::class.java.getResource("/cert/acs.keystore.p12")

    // Create a TCP server that will listen on port 27998
    // Secure the server with the keystore provided
    // Start the server and wait for it to terminate
    TcpServer.create()
        .port(PORT_AUTH)
        .secure { sslContextSpec -> sslContextSpec.sslContext(
            FileInputStream(File(keystore.toURI())),
            "hepl"
        )}
        .handle { inbound, outbound ->
            inbound.receive()
                .asByteArray()
                .log("IN")
                .map {
                    validate(it)
                }
                .flatMap {
                    outbound.sendByteArray(Mono.just(it))
                }
                .then()
        }
        .bindNow()
        .onDispose()
        //.block()
    TcpServer.create()
        .port(PORT_MONEY)
        .secure { sslContextSpec -> sslContextSpec.sslContext(
            FileInputStream(File(keystore.toURI())),
            "hepl"
        )}
        .handle { inbound, outbound ->
            inbound.receive()
                .asByteArray()
                .log("IN")
                .map {
                    validate(it)
                }
                .flatMap {
                    outbound.sendByteArray(Mono.just(it))
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

fun validate(payload : ByteArray) : ByteArray{
    val cardInfo : ArrayList<Int> = ArrayList(3)
    cardInfo.add(5545)
    cardInfo.add(8)
    cardInfo.add(23)

    //On a un message avec taille de signature (4 bytes) + message + signature
    val longueurPayload = payload.size
    var tailleSign = 0
    for (i in 0..3) {
        tailleSign = (tailleSign shl 8) + (payload.get(i) and 0xFF.toByte())
    }
    val signaturePayload = payload.copyOfRange(longueurPayload - tailleSign, longueurPayload)
    val message = payload.copyOfRange(4, longueurPayload - tailleSign)

    val verif: Boolean
    verif = try {
        val signatureLocale = Signature.getInstance("SHA1withRSA")
        signatureLocale.initVerify(getPublicKey())
        signatureLocale.update(message)
        signatureLocale.verify(signaturePayload)
    } catch (e: NoSuchAlgorithmException) {
        throw RuntimeException(e)
    } catch (e: InvalidKeyException) {
        throw RuntimeException(e)
    } catch (e: SignatureException) {
        throw RuntimeException(e)
    }

    if (verif) {
        var sentInfo = message.toString(Charset.defaultCharset()).split(".")
        if (sentInfo[0].toInt() == cardInfo[0] && sentInfo[2].toInt() == cardInfo[2] && sentInfo[2].toInt() == cardInfo[2]){
            return getToken()
        } else{
            return "Mauvaise carte".toByteArray(Charset.defaultCharset())
        }
    } else {
        return "Signature reçue: NOK".toByteArray(Charset.defaultCharset())
    }
}

fun getPublicKey() : PublicKey{
    //Récupération de la clé publique
    val publicKeyData = Application::class.java.getResource("/cert/client_public_key.der")
    val spec = X509EncodedKeySpec(publicKeyData.readBytes())
    val kf = KeyFactory.getInstance("RSA")
    return kf.generatePublic(spec)
}

fun getPrivateKey() : PrivateKey{
    val publicKeyData = Application::class.java.getResource("/cert/acs.keystore.p12")
    val keystore = KeyStore.getInstance(KeyStore.getDefaultType())
    keystore.load(publicKeyData.openStream(), "hepl".toCharArray())
    return keystore.getKey("acs", "hepl".toCharArray()) as PrivateKey
}

fun getToken() : ByteArray{
    //val header = Base64.getEncoder().encode(Json.encodeToString(Header("SHA1withRSA")).toByteArray())
    val token = Base64.getEncoder().encode(Random.nextBytes(2))
    tokens.add(token)
    val signature = Signature.getInstance("SHA1withRSA")
    signature.initSign(getPrivateKey())
    signature.update(token)
    val signed = signature.sign()
    return token + signed
}

fun verifyToken(token : Int) : String{
    for (i in 0..tokens.size){
        if (tokens[i].contentHashCode() == token){
            tokens.removeAt(i)
            return "OK"
        }
    }
    return "NOK"

}
