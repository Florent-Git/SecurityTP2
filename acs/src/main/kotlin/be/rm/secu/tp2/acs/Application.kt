package be.rm.secu.tp2.acs

import be.rm.secu.tp2.acs.card.HardcodedCardCodeProvider
import be.rm.secu.tp2.acs.handlers.AuthServerRequestHandler
import be.rm.secu.tp2.acs.handlers.MoneyServerRequestHandler
import be.rm.secu.tp2.acs.rsaotp.SignatureAlgorithm
import be.rm.secu.tp2.acs.rsaotp.TimeBasedSignatureOneTimePasswordConfig
import be.rm.secu.tp2.core.io.IO
import be.rm.secu.tp2.core.net.BasicServer
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

object Application

fun main() {
    val keyStore = Application::class.java.getResourceAsStream("/cert/acs.keystore.p12").use { inputStream ->
        inputStream?.let { IO.readKeystore(it, "hepl") } ?: throw Exception("Keystore not found")
    }

    val acsClientPubKey = Application::class.java.getResourceAsStream("/cert/acs-client.crt").use { inputStream ->
        inputStream?.let { IO.readCertificate(it).publicKey } ?: throw Exception("Could not find acs-client.crt")
    }

    val totpConfig = TimeBasedSignatureOneTimePasswordConfig(
        30L,
        TimeUnit.SECONDS,
        6,
        SignatureAlgorithm.SHA256
    )

    val authServer = BasicServer(
        27998, keyStore, "acs", "hepl", AuthServerRequestHandler(
            acsClientPubKey,
            HardcodedCardCodeProvider,
            totpConfig
        )
    )

    val moneyServer = BasicServer(
        9276, keyStore, "acs", "hepl", MoneyServerRequestHandler(
            keyStore,
            totpConfig
        )
    )

    thread {
        runBlocking {
            authServer.run()
        }
    }

    thread {
        runBlocking {
            moneyServer.run()
        }
    }

    print("Press any key to exit")
    readlnOrNull()

    moneyServer.stop()
    authServer.stop()
}

