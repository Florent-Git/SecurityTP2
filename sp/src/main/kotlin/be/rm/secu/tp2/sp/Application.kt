package be.rm.secu.tp2.sp

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import be.rm.secu.tp2.sp.plugins.*
import be.rm.secu.tp2.sp.plugins.configureRouting
import be.rm.secu.tp2.sp.plugins.configureSecurity
import be.rm.secu.tp2.sp.plugins.configureSerialization
import be.rm.secu.tp2.sp.plugins.configureTemplating
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureSecurity()
    configureTemplating()
    configureSerialization()
    configureRouting()
}
