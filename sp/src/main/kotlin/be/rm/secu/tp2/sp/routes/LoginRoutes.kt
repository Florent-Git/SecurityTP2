package be.rm.secu.tp2.sp.routes

import be.rm.secu.tp2.sp.plugins.BasicUserSession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.pebble.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.loginRoutes() {
    routing {
        authenticate("auth-form") {
            post("/login") {
                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                call.sessions.set(BasicUserSession(name = userName, count = 1))
                call.respondRedirect("/")
            }
        }

        get("/login"){
            call.respond(PebbleContent("login.html", emptyMap()))
        }

        get("/logout") {
            call.sessions.clear<BasicUserSession>()
            call.respondRedirect("/login")
        }
    }
}