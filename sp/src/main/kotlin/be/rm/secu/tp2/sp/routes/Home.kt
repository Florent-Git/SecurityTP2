package be.rm.secu.tp2.sp.routes

import io.ktor.server.application.*
import io.ktor.server.pebble.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.home() {
    routing {
        get("/") {
            call.respond(PebbleContent("index.html", emptyMap()))
        }
    }
}