package be.rm.secu.tp2.`acs-client`

import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import reactor.netty.tcp.TcpClient
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.security.*
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.Scanner

object Application
fun main() {
    val scanner = Scanner(System.`in`)
    val numCard:String
    val monthCard:String
    val yearCard:String

    System.out.println("=== Get your access token for payment ===")
    System.out.print("Enter the card number : ")
    numCard = scanner.next()

    System.out.print("Enter the card month of expiration : ")
    monthCard = scanner.next()

    System.out.print("Enter the card year of expiration : ")
    yearCard = scanner.next()

    //Création du message
    val cardInfo = numCard + "." + monthCard + "." + yearCard
    val signature = getSignature(cardInfo)
    val out = ByteArrayOutputStream()
    val dataStream = DataOutputStream(out)
    dataStream.writeInt(signature.size);
    out.write(cardInfo.toByteArray(Charset.defaultCharset()));
    out.write(signature);

    val client = TcpClient.create()
        /*.host("acs.tp2.secu.rm.be")*/
        .port(27998)
        .secure { sslContextSpec ->
            sslContextSpec.sslContext(getTruststore())
        }
        .connectNow()

    runBlocking {
        launch(Dispatchers.IO) {
            client.outbound()
                .sendByteArray(Mono.just(out.toByteArray()))
                .then()
                .log("OUT")
                .awaitFirstOrNull()
        }
    }

    runBlocking {
        val request = client.inbound()
            .receive()
            .asByteArray()
            .awaitFirst()

        verifySignature(request)
    }
}

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

fun getSignature(message : String): ByteArray{
    //Récupération de la clé privée
    val privateKeyData = Application::class.java.getResource("/cert/client_private_key.der")
    val spec = PKCS8EncodedKeySpec(privateKeyData.readBytes())
    val kf = KeyFactory.getInstance("RSA")
    val privateKey = kf.generatePrivate(spec)

    //Signature
    val sign = Signature.getInstance("SHA1withRSA")
    sign.initSign(privateKey)
    sign.update(message.toByteArray(Charset.defaultCharset()))
    return sign.sign()
}

fun verifySignature(signature : ByteArray){
    //Récupération de la clé publique
    val publicKeyData = Application::class.java.getResource("/cert/acs.keystore.p12")
    val keystore = KeyStore.getInstance(KeyStore.getDefaultType())
    keystore.load(publicKeyData.openStream(), "hepl".toCharArray())
    val publicKey = keystore.getCertificate("acs").publicKey

    //Vérification
    val tokenSize = signature.size
    if (tokenSize > 256){
        val signatureToken = signature.copyOfRange(tokenSize - 256, tokenSize)
        val message = signature.copyOfRange(0, tokenSize - 256)
        val verif = try {
            val signatureLocale = Signature.getInstance("SHA1withRSA")
            signatureLocale.initVerify(publicKey)
            signatureLocale.update(message)
            signatureLocale.verify(signatureToken)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        } catch (e: SignatureException) {
            throw RuntimeException(e)
        }
        if (verif){
            println(message.contentHashCode())
        }else{
            println("Le token reçu ne provient pas de l'ACS")
        }
    }else{
        println(signature.toString(Charset.defaultCharset()))
    }

}