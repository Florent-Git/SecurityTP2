package be.rm.secu.tp2.acq

import be.rm.secu.tp2.core.io.IO
import be.rm.secu.tp2.core.net.BasicClient
import be.rm.secu.tp2.core.net.BasicServer
import kotlinx.coroutines.runBlocking
import java.util.logging.Logger
import kotlin.concurrent.thread

object Application

fun main() {
    val logger = Logger.getLogger(Application::class.java.name)

    val acqKeystore = Application::class.java.getResourceAsStream("/cert/acq.keystore.p12").use { inputStream ->
        inputStream?.let { IO.readKeystore(it, "hepl") } ?: throw Exception("Keystore not found")
    }

    val caCertificate = Application::class.java.getResourceAsStream("/cert/ca.crt").use { inputStream ->
        inputStream?.let { IO.readCertificate(it) } ?: throw Exception("CA certificate not found")
    }

    val client = BasicClient(
        "acs.tp2.secu.rm.be",
        9798,
        caCertificate
    )

    val requestHandler = AcqServerRequestHandler(
        client
    )

    val server = BasicServer(
        9568,
        acqKeystore,
        "acq",
        "hepl",
        requestHandler
    )

    thread {
        runBlocking {
            server.run()
        }
    }

    logger.info("Server started")
    readlnOrNull()
    logger.info("Stopping server")
    server.stop()
    logger.info("Server stopped")
}
