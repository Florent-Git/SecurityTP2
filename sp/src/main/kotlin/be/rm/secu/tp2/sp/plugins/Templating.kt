package be.rm.secu.tp2.sp.plugins

import com.mitchellbosecke.pebble.loader.ClasspathLoader
import io.ktor.server.pebble.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.io.File

fun Application.configureTemplating() {
    install(Pebble) {
        loader(ClasspathLoader().apply {
            prefix = "templates"
        })
    }

    routing {
        get("/pebble-index") {
            val sampleUser = PebbleUser(1, "John")
            call.respond(PebbleContent("pebble-index.html", mapOf("user" to sampleUser)))
        }

        get("/login"){
            call.respondFile(File("sp/src/main/resources/templates/login.html"))
        }

        get("/logout") {
            call.sessions.clear<BasicUserSession>()
            call.respondRedirect("/login")
        }
    }
}

data class PebbleUser(val id: Int, val name: String)
