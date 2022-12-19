package be.rm.secu.tp2.sp.routes

import be.rm.secu.tp2.sp.config.getTruststore
import be.rm.secu.tp2.sp.plugins.BasicUserSession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.pebble.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.netty.handler.ssl.SslContextBuilder
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import reactor.core.publisher.Mono
import reactor.netty.tcp.TcpClient

fun Application.payRoutes() {
    routing {
        authenticate("auth-session") {
            get("/pay") {
                val basicUserSession = call.principal<BasicUserSession>()
                call.sessions.set(basicUserSession?.copy(count = basicUserSession.count + 1))
                call.respond(PebbleContent("/pay.html", mapOf("user" to basicUserSession!!)))
            }

            post("/pay") {
                // Receive the request from the HTTP POST request as a form parameter named "request"
                val request = call.receiveParameters()["amount"] ?: "No request"

                // Create a TCP socket that will send a request to the server on the port 27998
                // Create the client with reactor-netty
                val client = TcpClient.create()
                    .host("acq.tp2.secu.rm.be")
                    .port(9276)
                    .secure {
                        it.sslContext(
                            SslContextBuilder
                                .forClient()
                                .trustManager(getTruststore())
                                .build()
                        )
                    }
                    .connectNow()

                // Send the request to the server
                client.outbound()
                    .sendString(Mono.just(request))
                    .then()
                    .log("OUT")
                    .awaitFirstOrNull()

                // Receive the response from the server
                val response = client.inbound()
                    .receive()
                    .asString()
                    .awaitFirst()

                // Send the response to the client
                call.respond(response)
            }
        }
    }
}


