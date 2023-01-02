package be.rm.secu.tp2.sp.routes

import be.rm.secu.tp2.sp.plugins.BasicUserSession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.pebble.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.home() {
    routing {
        get("/") {
            val user = call.principal() ?: BasicUserSession("anonymous", 0)

            // Respond the index page with the Products object
            call.respond(PebbleContent("/index.html", mapOf("user" to user)))
        }
    }
}