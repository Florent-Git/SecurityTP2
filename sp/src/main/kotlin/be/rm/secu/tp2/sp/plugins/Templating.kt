package be.rm.secu.tp2.sp.plugins

import com.mitchellbosecke.pebble.loader.ClasspathLoader
import io.ktor.server.pebble.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

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
    }
}

data class PebbleUser(val id: Int, val name: String)
