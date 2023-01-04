package be.rm.secu.tp2.acs

import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

object Application

fun main() {
//    // Retrieve /certs/acq.p12 from the classpath
//    val keystore = Application::class.java.getResourceAsStream("/cert/acs.keystore.p12")
//
//    // Create a TCP server that will listen on port 27998
//    // Secure the server with the keystore provided
//    // Start the server and wait for it to terminate
//    TcpServer.create()
//        .port(27998)
//        .secure {
//            it.sslContext(SSL.createKeystoreSSLContext(keystore, "acs", "acs"))
//        }
//        .handle { inbound, outbound ->
//            inbound.receive()
//                .asString()
//                .log("IN")
//                .map { ACSClientRequest. }
//                .map(ACSClientRequestHandler::handleRequest)
//                .map { Json.decodeFromString<ACSClientRequest>(it) }
//                .flatMap {
//                    outbound.sendString(Mono.just(it))
//                }
//                .then()
//        }
//        .bindNow()
//        .onDispose()
//        .block()

    thread {
        runBlocking {
            AuthServer.run()
        }
    }

    print("Press any key to exit")
    readlnOrNull()

    MoneyServer.stop()
    AuthServer.stop()
}

