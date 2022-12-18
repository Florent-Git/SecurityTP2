package be.rm.secu.tp2.sp

import be.rm.secu.tp2.core.Db
import io.ktor.server.application.*
import be.rm.secu.tp2.sp.plugins.*
import be.rm.secu.tp2.sp.plugins.configureRouting
import be.rm.secu.tp2.sp.plugins.configureSerialization
import be.rm.secu.tp2.sp.plugins.configureTemplating
import be.rm.secu.tp2.sp.routes.tcpCall
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    Db.initDatabase()
    configureTemplating()
    configureSerialization()
    configureAuthentication()
    configureRouting()

    tcpCall()
}
