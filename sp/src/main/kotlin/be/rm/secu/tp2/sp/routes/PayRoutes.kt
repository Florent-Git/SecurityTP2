package be.rm.secu.tp2.sp.routes

import be.rm.secu.tp2.core.io.IO
import be.rm.secu.tp2.core.net.BasicClient
import be.rm.secu.tp2.sp.KtorApplication
import be.rm.secu.tp2.sp.plugins.BasicUserSession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.pebble.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

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
                val request = call.receiveParameters()["token"] ?: "No request"

                val certificate = KtorApplication::class.java.getResourceAsStream("/cert/ca.crt").use { inputStream ->
                    inputStream?.let { IO.readCertificate(it) } ?: throw Exception("No certificate")
                }

                // Create a TCP socket that will send a request to the server on the port 27998
                // Create the client with reactor-netty
                val client = BasicClient(
                    "acq.tp2.secu.rm.be",
                    9568,
                    certificate
                )

                val response = client.sendRequest(request)

                // Send the response to the client
                call.respond(response)
            }
        }
    }
}


