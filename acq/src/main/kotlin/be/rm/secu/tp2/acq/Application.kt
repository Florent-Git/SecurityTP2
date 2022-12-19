package be.rm.secu.tp2.acq

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import reactor.core.publisher.Mono

object Application

fun main(): Unit = runBlocking {
    val server = createServer(9276)
    val acs by lazy { createClient("acs.tp2.secu.rm.be", 27998) }

    launch(Dispatchers.IO) {
        val con = acs.handle { inbound, outbound ->
            inbound.receive()
                .asString()
                .log("IN ACS")
                .then()
        }.connectNow()

        con.outbound()
            .sendString(Mono.just("Hello ACS"))
            .awaitFirstOrNull()

        con.onDispose()
            .awaitFirstOrNull()
    }

    launch(Dispatchers.IO) {
        server.handle { inbound, outbound ->
            inbound.receive()
                .asString()
                .log("IN SP")
                .flatMap {
                    outbound
                        .sendString(Mono.just("Hello SP ! I received your pay: $it €"))
                        .then()
                }
                .then()
        }.bindNow()
            .onDispose()
            .awaitFirstOrNull()
    }
}
